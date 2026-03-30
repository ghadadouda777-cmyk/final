const Event = require('../models/Event');
const User = require('../models/User');

// @desc    Get all events
// @route   GET /api/events
// @access  Public
exports.getEvents = async (req, res) => {
  try {
    // Auto-update expired events status
    await Event.updateMany(
      { endDate: { $lt: new Date() }, status: { $ne: 'completed' } },
      { $set: { status: 'completed' } }
    );

    let query;
    const reqQuery = { ...req.query };

    // Fields to exclude from matching
    const removeFields = ['select', 'sort', 'page', 'limit', 'keyword'];
    removeFields.forEach((param) => delete reqQuery[param]);

    // Skip empty values
    Object.keys(reqQuery).forEach(key => {
      if (reqQuery[key] === '') delete reqQuery[key];
    });

    let queryStr = JSON.stringify(reqQuery);
    // Create operators ($gt, $gte, etc)
    queryStr = queryStr.replace(/\b(gt|gte|lt|lte|in)\b/g, (match) => `$${match}`);

    let queryParsed = JSON.parse(queryStr);

    // Lifecycle: prevent returning expired events unless explicitly asked
    if (req.query.status !== 'completed' && req.query.includeExpired !== 'true') {
      queryParsed.endDate = { $gte: new Date() };
    }

    query = Event.find(queryParsed);

    // Keyword search (title & tags)
    if (req.query.keyword) {
      query = query.find({
        $or: [
          { title: { $regex: req.query.keyword, $options: 'i' } },
          { tags: { $in: [new RegExp(req.query.keyword, 'i')] } }
        ]
      });
    }

    // Sort
    if (req.query.sort) {
      const sortBy = req.query.sort.split(',').join(' ');
      query = query.sort(sortBy);
    } else {
      query = query.sort('-createdAt');
    }

    // Pagination
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 10;
    const startIndex = (page - 1) * limit;
    const endIndex = page * limit;
    const total = await Event.countDocuments(query.getFilter());

    query = query.skip(startIndex).limit(limit);

    const events = await query;

    // Pagination result
    const pagination = {};
    if (endIndex < total) {
      pagination.next = { page: page + 1, limit };
    }
    if (startIndex > 0) {
      pagination.prev = { page: page - 1, limit };
    }

    res.status(200).json({
      success: true,
      count: events.length,
      pagination,
      data: events,
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Get single event
// @route   GET /api/events/:id
// @access  Public
exports.getEvent = async (req, res) => {
  try {
    console.log('DEBUG: Controller hit -> getEvent (ID:', req.params.id, ')');
    const event = await Event.findById(req.params.id)
      .populate('organizer', 'fullName profilePicture');

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }
    res.status(200).json({ success: true, data: event });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Create new event
// @route   POST /api/events
// @access  Private (Organizer, Admin)
exports.createEvent = async (req, res) => {
  try {
    // Add user to req.body
    req.body.organizer = req.user.id;

    // Lifecycle: prevent past event creation
    if (req.body.startDate && new Date(req.body.startDate) < new Date()) {
      return res.status(400).json({ success: false, message: 'Cannot create an event in the past' });
    }
    if (req.body.startDate && req.body.endDate && new Date(req.body.endDate) <= new Date(req.body.startDate)) {
      return res.status(400).json({ success: false, message: 'End date must be after start date' });
    }

    const event = await Event.create(req.body);

    // Add to user createdEvents
    await User.findByIdAndUpdate(req.user.id, {
      $push: { createdEvents: event._id }
    });

    res.status(201).json({ success: true, data: event });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Update event
// @route   PUT /api/events/:id
// @access  Private
exports.updateEvent = async (req, res) => {
  try {
    let event = await Event.findById(req.params.id);

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }

    // Ensure user is organizer or admin
    if (event.organizer.toString() !== req.user.id && req.user.role !== 'Admin') {
      return res.status(403).json({ success: false, message: 'Not authorized to update' });
    }

    console.log('DEBUG: updateEvent hit for ID:', req.params.id);
    console.log('Update Data:', req.body);

    event = await Event.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });
    console.log('Event updated in DB');

    res.status(200).json({ success: true, data: event });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Delete event
// @route   DELETE /api/events/:id
// @access  Private
exports.deleteEvent = async (req, res) => {
  try {
    const event = await Event.findById(req.params.id);

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }

    if (event.organizer.toString() !== req.user.id && req.user.role !== 'Admin') {
      return res.status(403).json({ success: false, message: 'Not authorized to delete' });
    }

    await event.deleteOne();

    // Remove from user's createdEvents
    await User.findByIdAndUpdate(event.organizer, {
      $pull: { createdEvents: event._id }
    });

    res.status(200).json({ success: true, data: {} });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

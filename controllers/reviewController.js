const Review = require('../models/Review');
const Event = require('../models/Event');

// @desc    Add review to an event
// @route   POST /api/events/:eventId/reviews
// @access  Private (Participant)
exports.addReview = async (req, res) => {
  try {
    const event = await Event.findById(req.params.eventId);
    if (!event) return res.status(404).json({ success: false, message: 'Event not found' });

    // Ensure the user hasn't already reviewed this event
    const existingReview = await Review.findOne({ event: event._id, user: req.user.id });
    if (existingReview) {
      return res.status(400).json({ success: false, message: 'You have already reviewed this event' });
    }

    req.body.user = req.user.id;
    req.body.event = event._id;

    const review = await Review.create(req.body);

    // Update event average rating (simplified)
    const reviews = await Review.find({ event: event._id });
    const avgRating = reviews.reduce((acc, item) => item.rating + acc, 0) / reviews.length;
    event.averageRating = avgRating;
    await event.save();

    res.status(201).json({ success: true, data: review });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Get reviews for an event
// @route   GET /api/events/:eventId/reviews
// @access  Public
exports.getReviews = async (req, res) => {
  try {
    const reviews = await Review.find({ event: req.params.eventId }).populate('user', 'fullName profilePicture');
    res.status(200).json({ success: true, count: reviews.length, data: reviews });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

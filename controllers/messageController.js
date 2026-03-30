const Message = require('../models/Message');

// @desc    Get all messages for an event
// @route   GET /api/messages/:eventId
// @access  Private (Registered Users only)
exports.getEventMessages = async (req, res) => {
  try {
    const messages = await Message.find({ event: req.params.eventId })
      .populate('sender', 'fullName profilePicture')
      .sort('createdAt');

    res.status(200).json({ success: true, count: messages.length, data: messages });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Create a new message
// @route   POST /api/messages/:eventId
// @access  Private
exports.createMessage = async (req, res) => {
  try {
    const message = await Message.create({
      event: req.params.eventId,
      sender: req.user.id,
      content: req.body.content,
    });

    const populatedMessage = await Message.findById(message._id).populate(
      'sender',
      'fullName profilePicture'
    );

    res.status(201).json({ success: true, data: populatedMessage });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

const Comment = require('../models/Comment');
const Event = require('../models/Event');

// @desc    Get comments for an event
// @route   GET /api/comments/:eventId
// @access  Public
exports.getComments = async (req, res) => {
  try {
    const comments = await Comment.find({ event: req.params.eventId })
      .populate('user', 'fullName profilePicture')
      .sort('-createdAt');

    res.status(200).json({ success: true, count: comments.length, data: comments });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Add comment to event
// @route   POST /api/comments/:eventId
// @access  Private
exports.addComment = async (req, res) => {
  try {
    const event = await Event.findById(req.params.eventId);
    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }

    const { text, rating } = req.body;

    const comment = await Comment.create({
      text,
      rating,
      event: req.params.eventId,
      user: req.user.id,
    });

    // Optionally: Update event average rating here

    res.status(201).json({ success: true, data: comment });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Delete comment
// @route   DELETE /api/comments/:id
// @access  Private
exports.deleteComment = async (req, res) => {
  try {
    const comment = await Comment.findById(req.params.id);

    if (!comment) {
      return res.status(404).json({ success: false, message: 'Comment not found' });
    }

    if (comment.user.toString() !== req.user.id && req.user.role !== 'Admin') {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    await comment.deleteOne();

    res.status(200).json({ success: true, data: {} });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

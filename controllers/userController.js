const User = require('../models/User');
const Event = require('../models/Event');
const Report = require('../models/Report');

// @desc    Get admin stats
// @route   GET /api/users/stats
// @access  Private/Admin
exports.getAdminStats = async (req, res) => {
  try {
    const totalUsers = await User.countDocuments();
    const totalEvents = await Event.countDocuments();
    const pendingReports = await Report.countDocuments({ status: 'pending' });

    res.status(200).json({
      success: true,
      data: {
        totalUsers,
        totalEvents,
        pendingReports
      }
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Get user profile
// @route   GET /api/users/profile
// @access  Private
exports.getUserProfile = async (req, res) => {
  try {
    const user = await User.findById(req.user._id)
      .populate({
        path: 'registeredEvents',
        select: 'title startDate bannerImage location category status'
      })
      .populate({
        path: 'createdEvents',
        select: 'title startDate bannerImage ticketsSold status'
      });

    if (user) {
      res.json({ success: true, data: user });
    } else {
      res.status(404).json({ success: false, message: 'User not found' });
    }
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Update user profile
// @route   PUT /api/users/profile
// @access  Private
exports.updateUserProfile = async (req, res) => {
  try {
    const user = await User.findById(req.user._id);

    if (user) {
      user.fullName = req.body.fullName || user.fullName;
      user.bio = req.body.bio || user.bio;

      if (req.body.contactInfo) {
        user.contactInfo = { ...user.contactInfo, ...req.body.contactInfo };
      }
      if (req.body.socialLinks) {
        user.socialLinks = { ...user.socialLinks, ...req.body.socialLinks };
      }
      if (req.body.privacySettings) {
        user.privacySettings = { ...user.privacySettings, ...req.body.privacySettings };
      }
      if (req.body.preferences) {
        user.preferences = { ...user.preferences, ...req.body.preferences };
      }

      if (req.body.password) {
        user.password = req.body.password; // pre-save hook will hash it
      }

      const updatedUser = await user.save();

      res.json({
        success: true,
        data: {
          _id: updatedUser._id,
          fullName: updatedUser.fullName,
          email: updatedUser.email,
          role: updatedUser.role,
        },
      });
    } else {
      res.status(404).json({ success: false, message: 'User not found' });
    }
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Change password
// @route   PUT /api/users/password
// @access  Private
exports.changePassword = async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;
    const user = await User.findById(req.user._id).select('+password');

    if (!user || !(await user.matchPassword(currentPassword))) {
      return res.status(401).json({ success: false, message: 'Invalid current password' });
    }

    user.password = newPassword;
    await user.save();

    res.status(200).json({ success: true, message: 'Password updated successfully' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Upload user avatar
// @route   POST /api/users/profile/avatar
// @access  Private
exports.uploadAvatar = async (req, res) => {
  try {
    const user = await User.findById(req.user._id);
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    if (req.file) {
      let avatarUrl = req.file.path;
      if (!avatarUrl.startsWith('http')) {
        avatarUrl = `/uploads/${req.file.filename}`;
      }
      user.profilePicture = avatarUrl;
      await user.save();
      res.json({ success: true, message: 'Avatar uploaded', avatarUrl: user.profilePicture });
    } else {
      res.status(400).json({ success: false, message: 'Please upload a file' });
    }
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Get current organizer's events
// @route   GET /api/users/my-events
// @access  Private (Organizer, Admin)
exports.getMyEvents = async (req, res) => {
  try {
    const events = await Event.find({ organizer: req.user._id }).sort('-createdAt');
    res.status(200).json({
      success: true,
      count: events.length,
      data: events,
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};
// @desc    Get all users
// @route   GET /api/users
// @access  Private (Admin)
exports.getAllUsers = async (req, res) => {
  try {
    const users = await User.find({}).select('-password').sort('-createdAt');
    res.status(200).json({
      success: true,
      count: users.length,
      data: users,
    });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

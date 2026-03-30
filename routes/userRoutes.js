const express = require('express');
const {
  getUserProfile,
  updateUserProfile,
  uploadAvatar,
  getAdminStats,
  getMyEvents,
  changePassword,
  getAllUsers
} = require('../controllers/userController');
const { protect, authorize } = require('../middlewares/authMiddleware');
const upload = require('../middlewares/uploadMiddleware');

const router = express.Router();

router.get('/', protect, authorize('Admin'), getAllUsers);
router.get('/stats', protect, authorize('Admin'), getAdminStats);

router.route('/profile')
  .get(protect, getUserProfile)
  .put(protect, updateUserProfile);

router.put('/password', protect, changePassword);

router.post('/profile/avatar', protect, upload.single('image'), uploadAvatar);

router.get('/my-events', protect, authorize('Organizer', 'Admin'), getMyEvents);

module.exports = router;

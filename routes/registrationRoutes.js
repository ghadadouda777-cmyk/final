const express = require('express');
const {
  registerForEvent,
  cancelRegistration,
  getMyRegistrations
} = require('../controllers/registrationController');
const { protect } = require('../middlewares/authMiddleware');

const router = express.Router();

router.post('/:eventId', protect, registerForEvent);
router.put('/:id/cancel', protect, cancelRegistration);
router.get('/my', protect, getMyRegistrations);

module.exports = router;

const express = require('express');
const { getEventMessages, createMessage } = require('../controllers/messageController');
const { protect } = require('../middlewares/authMiddleware');

const router = express.Router();

router.get('/:eventId', protect, getEventMessages);
router.post('/:eventId', protect, createMessage);

module.exports = router;

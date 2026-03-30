const express = require('express');
const { getComments, addComment, deleteComment } = require('../controllers/commentController');
const { protect } = require('../middlewares/authMiddleware');

const router = express.Router();

router.get('/:eventId', getComments);
router.post('/:eventId', protect, addComment);
router.delete('/:id', protect, deleteComment);

module.exports = router;

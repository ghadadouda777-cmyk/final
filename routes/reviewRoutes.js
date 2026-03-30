const express = require('express');
const { addReview, getReviews } = require('../controllers/reviewController');
const { protect } = require('../middlewares/authMiddleware');

// Using mergeParams: true allows access to :eventId from the parent router
const router = express.Router({ mergeParams: true });

router.route('/')
  .post(protect, addReview)
  .get(getReviews);

module.exports = router;

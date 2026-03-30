const express = require('express');
const {
  createReport,
  getReports,
  updateReport
} = require('../controllers/reportController');
const { protect, authorize } = require('../middlewares/authMiddleware');

const router = express.Router();

router.route('/')
  .post(protect, createReport)
  .get(protect, authorize('Admin'), getReports);

router.route('/:id')
  .put(protect, authorize('Admin'), updateReport);

module.exports = router;

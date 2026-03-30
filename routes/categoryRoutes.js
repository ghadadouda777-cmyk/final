const express = require('express');
const {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
} = require('../controllers/categoryController');
const { protect, authorize } = require('../middlewares/authMiddleware');

const router = express.Router();

router
  .route('/')
  .get(getCategories)
  .post(protect, authorize('Admin'), createCategory);

router
  .route('/:id')
  .put(protect, authorize('Admin'), updateCategory)
  .delete(protect, authorize('Admin'), deleteCategory);

module.exports = router;

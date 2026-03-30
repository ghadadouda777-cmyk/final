const express = require('express');
const router = express.Router();
const upload = require('../middlewares/uploadMiddleware');

router.post('/', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ success: false, message: 'Please upload a file' });
  }
  
  // Cloudinary returns the full URL in req.file.path
  // Multer Disk storage returns the system path in req.file.path, but we want the web path
  let filePath = req.file.path;
  
  if (!req.file.path.startsWith('http')) {
     filePath = `/uploads/${req.file.filename}`;
  }
  
  res.status(200).json({ success: true, data: filePath });
});

module.exports = router;

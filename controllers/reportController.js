const Report = require('../models/Report');

// @desc    Create a report
// @route   POST /api/reports
// @access  Private
exports.createReport = async (req, res) => {
  try {
    req.body.reporter = req.user.id;

    const report = await Report.create(req.body);

    res.status(201).json({ success: true, data: report });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Get all reports (Admin)
// @route   GET /api/reports
// @access  Private/Admin
exports.getReports = async (req, res) => {
  try {
    const reports = await Report.find().populate('reporter', 'fullName email').sort('-createdAt');
    res.status(200).json({ success: true, count: reports.length, data: reports });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Update report status
// @route   PUT /api/reports/:id
// @access  Private/Admin
exports.updateReport = async (req, res) => {
  try {
    let report = await Report.findById(req.params.id);

    if (!report) {
      return res.status(404).json({ success: false, message: 'Report not found' });
    }

    report = await Report.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true
    });

    res.status(200).json({ success: true, data: report });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

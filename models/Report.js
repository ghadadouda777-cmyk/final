const mongoose = require('mongoose');

const reportSchema = new mongoose.Schema(
  {
    reporter: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    reportedItemId: {
      type: mongoose.Schema.Types.ObjectId,
      required: true,
      // Ref can be dynamic but for strict schema we keep it generic and use itemType to interpret
    },
    itemType: {
      type: String,
      enum: ['User', 'Event', 'Review'],
      required: true,
    },
    reason: {
      type: String,
      required: [true, 'Please provide a reason for the report'],
      maxlength: [1000, 'Reason cannot exceed 1000 characters'],
    },
    status: {
      type: String,
      enum: ['pending', 'reviewed', 'resolved', 'dismissed'],
      default: 'pending',
    },
    adminNotes: {
      type: String,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model('Report', reportSchema);

const mongoose = require('mongoose');

const registrationSchema = new mongoose.Schema(
  {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    event: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Event',
      required: true,
    },
    tickets: [
      {
        ticketType: {
          type: mongoose.Schema.Types.ObjectId,
          ref: 'Ticket',
        },
        quantity: {
          type: Number,
          required: true,
          default: 1,
        },
      },
    ],
    totalAmount: {
      type: Number,
      default: 0,
    },
    registrationStatus: {
      type: String,
      enum: ['pending', 'confirmed', 'cancelled', 'waitlisted'],
      default: 'confirmed', // Simplifying for now, usually pending until payment
    },
    paymentStatus: {
      type: String,
      enum: ['pending', 'completed', 'failed', 'refunded'],
      default: 'completed', // Assuming free MVP
    },
    qrCode: {
      type: String, // URL to QR code image
    },
    checkInStatus: {
      type: Boolean,
      default: false,
    },
    checkInTime: {
      type: Date,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model('Registration', registrationSchema);

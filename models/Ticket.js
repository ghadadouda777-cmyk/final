const mongoose = require('mongoose');

const ticketSchema = new mongoose.Schema(
  {
    event: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Event',
      required: true,
    },
    name: {
      type: String,
      required: [true, 'Please add a ticket name (e.g., VIP, Standard)'],
      trim: true,
    },
    price: {
      type: Number,
      required: [true, 'Please add a ticket price. Set 0 for free.'],
      min: [0, 'Price cannot be negative'],
    },
    quantity: {
      type: Number,
      required: [true, 'Please specify the number of tickets available'],
    },
    sold: {
      type: Number,
      default: 0,
    },
    description: {
      type: String,
    },
    salesStart: {
      type: Date,
      default: Date.now,
    },
    salesEnd: {
      type: Date,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model('Ticket', ticketSchema);

const mongoose = require('mongoose');

const eventSchema = new mongoose.Schema(
  {
    title: {
      type: String,
      required: [true, 'Please add a title'],
      trim: true,
      maxlength: [100, 'Title cannot exceed 100 characters'],
    },
    description: {
      type: String,
      required: [true, 'Please add a description'],
      maxlength: [2000, 'Description cannot exceed 2000 characters'],
    },
    startDate: {
      type: Date,
      required: [true, 'Please add a starting date'],
    },
    endDate: {
      type: Date,
      required: [true, 'Please add an ending date'],
    },
    price: {
      type: Number,
      default: 0,
    },
    location: {
      address: {
        type: String,
        required: [true, 'Please add an address'],
      },
      city: String,
      country: String,
      coordinates: {
        lat: Number,
        lng: Number,
      },
    },
    organizer: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    category: {
      type: String,
      enum: [
        'Music & Concerts',
        'Conferences & Business',
        'Education & Classes',
        'Weddings & Celebrations',
        'Sports & Wellness',
        'Arts & Culture',
        'Gastronomy & Food',
        'Community & Social',
        'Technology & Gaming',
        'Parties & Leisure'
      ],
      required: true,
    },
    bannerImage: {
      type: String,
      default: 'default-event.jpg',
    },
    capacity: {
      type: Number,
      required: [true, 'Please specify the maximum capacity'],
      min: [1, 'Capacity must be at least 1'],
    },
    ticketsSold: {
      type: Number,
      default: 0,
    },
    registrationDeadline: {
      type: Date,
    },
    status: {
      type: String,
      enum: ['draft', 'published', 'completed', 'cancelled'],
      default: 'draft',
    },
    tags: [String],
    averageRating: {
      type: Number,
      min: [1, 'Rating must be at least 1'],
      max: [5, 'Rating cannot be more than 5'],
    },
    isOnline: {
      type: Boolean,
      default: false,
    },
    meetingLink: {
      type: String,
    },
  },
  {
    timestamps: true,
    toJSON: { virtuals: true },
    toObject: { virtuals: true },
  }
);

// Virtual for checking if the event is full
eventSchema.virtual('isFull').get(function () {
  return this.ticketsSold >= this.capacity;
});

module.exports = mongoose.model('Event', eventSchema);

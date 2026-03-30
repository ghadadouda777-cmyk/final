const Registration = require('../models/Registration');
const Event = require('../models/Event');
const Ticket = require('../models/Ticket');
const User = require('../models/User');
const sendEmail = require('../utils/sendEmail');

// @desc    Register for an event
// @route   POST /api/registrations/:eventId
// @access  Private
exports.registerForEvent = async (req, res) => {
  try {
    const event = await Event.findById(req.params.eventId);
    const user = await User.findById(req.user.id);

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }

    if (event.isFull) {
      return res.status(400).json({ success: false, message: 'Event is at full capacity' });
    }

    // Check if already registered
    const existingRegistration = await Registration.findOne({
      user: req.user.id,
      event: event._id,
      registrationStatus: { $ne: 'cancelled' }
    });

    if (existingRegistration) {
      return res.status(400).json({ success: false, message: 'You are already registered' });
    }

    const tickets = req.body.tickets || [];
    let totalAmount = 0;

    if (tickets.length > 0) {
      for (let t of tickets) {
        const ticketDoc = await Ticket.findById(t.ticketType);
        if (ticketDoc) {
          totalAmount += ticketDoc.price * t.quantity;
          if (ticketDoc.sold + t.quantity > ticketDoc.quantity) {
             return res.status(400).json({ success: false, message: `Not enough tickets left` });
          }
        }
      }
    } else {
      totalAmount = event.price || 0;
    }

    const registration = await Registration.create({
      user: req.user.id,
      event: event._id,
      tickets,
      totalAmount,
      registrationStatus: 'confirmed',
    });

    // Handle Admin Commission (5%)
    if (totalAmount > 0) {
      const commission = totalAmount * 0.05;
      // Find one admin to receive the commission (simplified for this exercise)
      const admin = await User.findOne({ role: 'Admin' });
      if (admin) {
        admin.balance += commission;
        await admin.save();
      }
    }

    event.ticketsSold += 1;
    await event.save();

    for (let t of tickets) {
      const ticketDoc = await Ticket.findById(t.ticketType);
      if (ticketDoc) {
        ticketDoc.sold += t.quantity;
        await ticketDoc.save();
      }
    }

    await User.findByIdAndUpdate(req.user.id, {
      $push: { registeredEvents: event._id }
    });

    // Send Confirmation Email
    try {
      await sendEmail({
        email: user.email,
        subject: `Event Registration Confirmed: ${event.title}`,
        message: `Hello ${user.fullName},\n\nYou have successfully registered for ${event.title}.`,
        html: `<h3>Hello ${user.fullName},</h3><p>You have successfully registered for <strong>${event.title}</strong>!</p><p>We look forward to seeing you there.</p>`
      });
    } catch (err) {
      console.error('Email could not be sent');
    }

    res.status(201).json({ success: true, data: registration });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Cancel registration
// @route   PUT /api/registrations/:id/cancel
// @access  Private
exports.cancelRegistration = async (req, res) => {
  try {
    const registration = await Registration.findById(req.params.id);

    if (!registration) {
      return res.status(404).json({ success: false, message: 'Registration not found' });
    }

    // Ensure authorized user
    if (registration.user.toString() !== req.user.id && req.user.role !== 'Admin') {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    if (registration.registrationStatus === 'cancelled') {
      return res.status(400).json({ success: false, message: 'Already cancelled' });
    }

    registration.registrationStatus = 'cancelled';
    await registration.save();

    // Adjust event tickets
    const event = await Event.findById(registration.event);
    if (event) {
      event.ticketsSold -= 1;
      await event.save();
    }

    // Adjust user profile array
    if (registration.user.toString() === req.user.id) {
       await User.findByIdAndUpdate(req.user.id, {
        $pull: { registeredEvents: event._id }
      });
    }

    res.status(200).json({ success: true, data: registration });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

// @desc    Get user registrations
// @route   GET /api/registrations/my
// @access  Private
exports.getMyRegistrations = async (req, res) => {
  try {
    const registrations = await Registration.find({ user: req.user.id })
      .populate('event', 'title startDate location bannerImage')
      .populate('tickets.ticketType', 'name price');

    res.status(200).json({ success: true, count: registrations.length, data: registrations });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

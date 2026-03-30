const Notification = require('../models/Notification');

/**
 * Create a new notification for a specific user.
 * @param {string} userId - ID of the user
 * @param {string} message - Notification text
 * @param {string} type - 'INFO', 'WARNING', 'SUCCESS', 'ERROR'
 * @param {string} link - Optional link to redirect on click
 */
const createNotification = async (userId, message, type = 'INFO', link = '') => {
  try {
    const notification = await Notification.create({
      user: userId,
      message,
      type,
      link
    });
    // In a real application, you might also emit a socket.io event here
    // for real-time frontend updates. 
    return notification;
  } catch (error) {
    console.error('Error creating notification:', error);
  }
};

module.exports = { createNotification };

// @desc    Process chatbot message
// @route   POST /api/chatbot
// @access  Public
exports.processMessage = async (req, res) => {
  try {
    const { message } = req.body;
    
    // Simple mock logic for MVP. If OpenAI key is provided, this could call the OpenAI SDK.
    const lowerMsg = message.toLowerCase();
    let botReply = "I'm a virtual assistant here to help! Ask me about events, registration, or platform features.";
    
    if (lowerMsg.includes('event') || lowerMsg.includes('find')) {
       botReply = "You can explore upcoming events by clicking 'Explore Events' in the navigation bar. You can filter by category or search by topic there.";
    } else if (lowerMsg.includes('register') || lowerMsg.includes('ticket')) {
       botReply = "To register for an event, make sure you are logged in, then visit the event details page and click 'Register Now'.";
    } else if (lowerMsg.includes('create') || lowerMsg.includes('organize')) {
       botReply = "If you're an organizer, you can create new events from your Dashboard or by navigating to /events/create.";
    }

    // Artificial delay to simulate AI thinking
    setTimeout(() => {
        res.status(200).json({ success: true, reply: botReply });
    }, 1000);
    
  } catch (error) {
    res.status(500).json({ success: false, message: 'Chatbot error' });
  }
};

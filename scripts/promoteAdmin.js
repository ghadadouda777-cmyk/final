const mongoose = require('mongoose');
const dotenv = require('dotenv');
const User = require('../models/User');

dotenv.config();

const createAdmin = async () => {
    try {
        await mongoose.connect(process.env.MONGO_URI);
        console.log('Connected to MongoDB...');

        const email = process.argv[2];
        if (!email) {
            console.error('Please provide an email: node createAdmin.js user@example.com');
            process.exit(1);
        }

        const user = await User.findOne({ email });
        if (!user) {
            console.error('User not found. Please register this email on the website first.');
            process.exit(1);
        }

        user.role = 'Admin';
        await user.save();

        console.log(`Success! ${email} is now an Admin.`);
        process.exit(0);
    } catch (error) {
        console.error('Error:', error.message);
        process.exit(1);
    }
};

createAdmin();

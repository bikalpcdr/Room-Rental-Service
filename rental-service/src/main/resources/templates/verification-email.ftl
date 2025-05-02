<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Email Verification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin: 20px 0;
        }
        .footer {
            margin-top: 30px;
            font-size: 12px;
            color: #666;
        }
    </style>
</head>
<body>
    <h2>Email Verification</h2>
    <p>Hello,</p>
    <p>Thank you for registering with our service. Please verify your email address by clicking the button below:</p>
    <p>
        <a href="${verificationUrl}" class="button">Verify Email</a>
    </p>
    <p>Or copy and paste this link into your browser:</p>
    <p>${verificationUrl}</p>
    <p>This link will expire in 24 hours.</p>
    <div class="footer">
        <p>If you have any questions, please contact our support team.</p>
        <p>This is an automated message, please do not reply to this email.</p>
    </div>
</body>
</html> 
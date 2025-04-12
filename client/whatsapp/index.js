const { Client } = require('whatsapp-web.js');
const qrcode = require('qrcode-terminal');

const client = new Client();

client.on('qr', (qr) => {
    qrcode.generate(qr, { small: true});
    console.log("QR-code was created");
});

client.on('ready', () => {
    console.log("Bot was connected");
})

client.initialize();

client.on('message', async (msg) => {
    // const chat = await msg.getChat();
    if (msg.body == 'hello') {
        msg.reply('Hello!');
    }
})


const { kafka } = require('./client');

//kafka.admin().createTopics

const sendMessage = async function() {
    const producer = kafka.producer();

    console.log("Connecting Producer");
    await producer.connect();
    console.log("Producer Connected Successfully");
  
    const location = 'north'
    // await producer.send({
    // topic: "baeldung",
    // messages: [
    //     {
    //     partition: location == 'north' ? 0 : 1,
    //     key: "location-update",
    //     value: JSON.stringify({ name: 'evgeni', location: location}),
    //     },
    // ],
    // });
    await producer.send({
        topic: "input-topic",
        messages: [
            {
            key: "wc-test",
            value: "hello world",
            },
        ],
        });
    await producer.disconnect();
    
}

sendMessage();
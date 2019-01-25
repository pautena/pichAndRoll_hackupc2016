## Inspiration
As it was a long 36 hours hackathon, we wanted to develop a product where we can learn and have fun using different technologies. But a point we have it clear from the begining, is that we also wanted to make a prototype that it's idea could be useful.

## What it does
HackEyes is a robot car controlled by air gestures. The user can also see throw Google Cardboard a first person view that it is connected into a camera that the car has.

When the user rotates his head it moves the camera that it is installed on the car, so it gives the illusion to be driving in real person the car.

## How I built it
First of all, we decided the technology to use, plan how we were going to work and printed the robot arm. 

Then we started building a custom Google Cardboard viewer so we could reproduce video simultaneously in both parts of the screen. 

The following step was to transmit video from the Raspberry Pi into that player, adapting it to accept livestream connection.

Next thing to do was programming the movement of the camera attached to the device building a rest api with flask, so the servos move together with the camera.

To finish, we connected Leap Motion to the Raspberry via a rest service and constructed the robot.

## Challenges I ran into
First of all, we thought about building the prototype with a phone attatched to the robot, but the robotic arm printed as not strong enough to support it. In that case, we decided to run to buy a smaller camera that we could program.

Our second problem came with the connection via streaming. We spend almost 10 consecutive hours trying to fix this bug.

The third challenge we had is that we didn't sleep so much.

## Accomplishments that I'm proud of

- The user can see what the robot it is seeing via streaming.
- If user moves the head, the camera will move to this position.
- The car it is controlled by using air gestures.
-It works and only in 36 hours!

## What I learned
Some people of the team didn't know about arduino or raspberry. Also no one of use didn't know anything about video streaming or leap motion.

## What's next for HackEyes

- Less latency between the signal send it to google cardboard!
- Implementation in other devices such as drones.

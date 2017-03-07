var connected = false;


var img1, img2, img3, img4, img5, img6;

var rotationAngleMousePosition = false;
var rotationAngle;
var rotationAngleLastPosition = 0;

var socket;

function setup() {
  img1 = loadImage("assets/Observatorium_Dial.png");
  img2 = loadImage("assets/Observatorium_Arrow.png");
  img3 = loadImage("assets/Observatorium_Star.png");
  img4 = loadImage("assets/Observatorium_Planet.png");
  img5 = loadImage("assets/Observatorium_Const.png");
  img6 = loadImage("assets/Observatorium_StartGal.png");

  createCanvas(windowWidth, windowHeight);

  // leave this line
  if (connected) socket = io('http://172.20.42.42:3000');

  // this is how we know we're connected
  if (connected) {
    socket.on('connect', function() {
      print(" I connected!! ");
    });
  }

}


function draw() {
  background(0);
  noStroke;
  fill(255);

  // the elements
  image(img3, ((windowWidth / 2) - (img4.width / 2)) - 180, windowHeight / 2, img3.width / 2, img3.height / 2)
  image(img4, (windowWidth / 2) - (img4.width / 2) / 2, windowHeight / 2, img4.width / 2, img4.height / 2);

  image(img5, ((windowWidth / 2) - (img4.width / 2)) + 275, windowHeight / 2, img5.width / 2, img5.height / 2);
  image(img6, 0, 0, windowWidth, img6.height / 5);
  
  

  //the dial
  if (mouseIsPressed && mouseY > windowHeight - 200) {
    if (!rotationAngleMousePosition) {
      rotationAngleMousePosition = atan2(mouseY - height, mouseX - width / 2);
    }

    translate(windowWidth / 2, windowHeight); //for the center
    rotationAngle = rotationAngleLastPosition + atan2(mouseY - height, mouseX - width / 2) - rotationAngleMousePosition;
    rotate(rotationAngle);
    image(img1, -125, -125, 250, 250);

    sendOsc("/rotationAngle", rotationAngle);
  } else {
    translate(windowWidth / 2, windowHeight); //for the center
    rotate(rotationAngle);
    image(img1, -125, -125, 250, 250);

  }

  function mouseReleased() {
    rotationAngleMousePosition = false;
    rotationAngleLastPosition = rotationAngle;
    // send something!
    // anywhere you want to send stuff, just do this:
    //sendOsc("/message", "value");

  }

}


// keep this!
function sendOsc(address, value) {
  if (connected) socket.emit('message', [address].concat(value));
}
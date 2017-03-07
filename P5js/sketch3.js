var connected = true;


var img1, img3, img4, img5, img6;
var currentDraggedImage = false;

var rotationAngleMousePosition = false;
var rotationAngle;
var rotationAngleLastPosition = 0;

var socket;

function setup() {
  img1 = loadImage("assets/Observatorium_Dial.png");
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

  // the star, planet and constellation
  // when each of these are dragged and dropped into the galaxy
  //in the top half of the screen, it creates a sound and appears in the server

  var image3X = ((windowWidth / 2) - (img4.width / 2)) - 180;
  var image3Y = windowHeight / 2;
  var image3Width = img3.width / 2;
  var image3Height = img3.height / 2;

  var image4X = (windowWidth / 2) - (img4.width / 2) / 2;
  var image4Y = windowHeight / 2;
  var image4Width = img4.width / 2;
  var image4Height = img4.height / 2;

  var image5X = ((windowWidth / 2) - (img4.width / 2)) + 275;
  var image5Y = windowHeight / 2;
  var image5Width = img5.width / 2;
  var image5Height = img5.height / 2;

  var image6Height = img6.height / 5;

  image(img3, image3X, image3Y, image3Width, image3Height);
  image(img4, image4X, image4Y, image4Width, image4Height);
  image(img5, image5X, image5Y, image5Width, image5Height);
  image(img6, 0, 0, windowWidth, image6Height);


  if (mouseIsPressed && mouseY < windowHeight - 350) {
    if (mouseX > image3X && mouseX < image3X + image3Width) {
      if (mouseY > image3Y && mouseY < image3Y + image3Height) {
        currentDraggedImage = 3;
      }
    }
    if (mouseX > image4X && mouseX < image4X + image4Width) {
      if (mouseY > image4Y && mouseY < image4Y + image4Height) {
        currentDraggedImage = 4;
      }
    }
    if (mouseX > image5X && mouseX < image5X + image5Width) {
      if (mouseY > image5Y && mouseY < image5Y + image5Height) {
        currentDraggedImage = 5;
      }
    }

    switch (currentDraggedImage) {
      case 3:
        image(img3, mouseX - image3Width / 2, mouseY - image3Height / 2, image3Width, image3Height);
        break;
      case 4:
        image(img4, mouseX - image4Width / 2, mouseY - image4Height / 2, image4Width, image4Height);
        break;
      case 5:
        image(img5, mouseX - image5Width / 2, mouseY - image5Height / 2, image5Width, image5Height);
        break;
    }

  }

  //the dial when rotated in the client machine also rotates the metronome on the server
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

}

function mouseReleased() {
  rotationAngleMousePosition = false;
  rotationAngleLastPosition = rotationAngle;
  if (currentDraggedImage != false && mouseY < 200) {
    console.log('currentDraggedImage');
    console.log(currentDraggedImage);
    switch (currentDraggedImage) {
      case 3: // STAR
        sendOsc("/celestialObject", "STAR;" + mouseX / windowWidth);
        break;
      case 4: // PLANET
        sendOsc("/celestialObject", "PLANET;" + mouseX / windowWidth);
        break;
      case 5: // CONST
        sendOsc("/celestialObject", "CONST;" + mouseX / windowWidth);
        break;
    }

  }

  currentDraggedImage = false;
  // send something!
  // anywhere you want to send stuff, just do this:
  //sendOsc("/message", "value");

}




// keep this!
function sendOsc(address, value) {
  if (connected) socket.emit('message', [address].concat(value));
}
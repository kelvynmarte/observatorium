var socket;

function setup() {
	createCanvas(1680, 989);
	background(0);
	
	// make a magic internet portal!
	// leave this line
	socket = io('http://172.20.42.42:3000');
	
	// this is how we know we're connected
	socket.on('connect', function() {
		print(" I connected!! ");
	});
}
var rotationAngle;

function draw() {
  background(204);
  noStroke;
  fill(0)
    if (mouseIsPressed) {
    translate(width / 2, height /2); //for the center
    rotationAngle = atan2(mouseY - height / 2, mouseX - width / 2);
    rotate(rotationAngle);
    rect(-60, -5, 120, 10);
    sendOsc("/rotationAngle", rotationAngle);
  } else {
    translate(width / 2, height /2); //for the center
    rotate(rotationAngle);
    rect(-60, -5, 120, 10);
  }
}



function mousePressed() {

  // send something!
  // anywhere you want to send stuff, just do this:
	//sendOsc("/message", "value");
	
}

// keep this!
function sendOsc(address, value) {
 	socket.emit('message', [address].concat(value));
}


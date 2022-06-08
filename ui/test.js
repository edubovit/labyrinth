const apiHost = 'http://labyrinth.edubovit.net/api/';

let session;

window.onload = async () => {
    const response = await fetch(`${apiHost}game/create`, {
        method: 'POST'
    });
    const body = await response.json();
    session = body.id;
    const newImageUrl = `${apiHost}${body.mapUrl}`;
    document.getElementById('image-test').src = newImageUrl;
}

window.onkeydown = event => {
    switch (event.key) {
        case 'w':
        case 'ArrowUp':
            move('up');
            break;
        case 'a':
        case 'ArrowLeft':
            move('left');
            break;
        case 's':
        case 'ArrowDown':
            move('down');
            break;
        case 'd':
        case 'ArrowRight':
            move('right');
            break;
        default:
    }
}

async function move(direction) {
    const response = await fetch(`${apiHost}game/${session}/${direction}`, {
        method: 'POST'
    });
    const body = await response.json();
    const newImageUrl = `${apiHost}${body.mapUrl}`;
    document.getElementById('image-test').src = newImageUrl;
}

const apiHost = 'http://localhost:8080/';

let session;

let turn = 0;

window.onload = async () => {
    const response = await fetch(`${apiHost}game/create`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: '{"width":80,"height":50,"cellSize":14}'
    });
    const body = await response.json();
    session = body.id;
    const newImageUrl = `${apiHost}${body.mapUrl.substring(1)}`;
    document.getElementById('image-test').src = newImageUrl;
    document.getElementById('counter').innerText = `Turn ${turn++}`;
}

window.onkeydown = event => {
    switch (event.key) {
        case 'w':
        case 'ц':
        case 'ArrowUp':
            move('up');
            break;
        case 'a':
        case 'ф':
        case 'ArrowLeft':
            move('left');
            break;
        case 's':
        case 'ы':
        case 'ArrowDown':
            move('down');
            break;
        case 'd':
        case 'в':
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
    const newImageUrl = `${apiHost}${body.mapUrl.substring(1)}`;
    document.getElementById('image-test').src = newImageUrl;
    document.getElementById('counter').innerText = `Turn ${turn++}`;
}

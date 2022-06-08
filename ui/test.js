const apiHost = 'http://localhost:8080/';

let imageTag = document.getElementById('image-test');
let session;
let imageUrl;
let turn = 0;

const difficulties = {
    zero: '{"width":3,"height":3,"cellSize":200,"cellBorder":5,"outerBorder":10}',
    easy: '{"width":10,"height":10,"cellSize":30}',
    medium: '{}',
    hard: '{"width":80,"height":50,"cellSize":16}',
    brutal: '{"width":120,"height":80,"cellSize":10}',
    cave: '{"width":10,"height":200}'
}

window.onload = async () => {
    imageTag = document.getElementById('image-test');
    initButtons();
    const url = new URL(window.location.href);
    const sessionMatch = url.pathname.match(/session\/([\da-f-]{36})/);
    if (sessionMatch && sessionMatch[1]) {
        session = sessionMatch[1];
    }
    if (session) {
        const response = await fetch(`${apiHost}game/${session}`);
        const body = await response.json();
        imageUrl = `${apiHost}${body.mapUrl.substring(1)}`;
    } else {
        await createGame(url.searchParams.get('difficulty'));
    }
    imageTag.src = imageUrl;
    document.getElementById('counter').innerText = `Turn ${turn++}`;
    document.getElementById('session').innerText = `Session: ${session}`;
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
    imageUrl = `${apiHost}${body.mapUrl.substring(1)}`;
    imageTag.src = imageUrl;
    document.getElementById('counter').innerText = `Turn ${turn++}`;
}

async function createGame(difficulty) {
    const response = await fetch(`${apiHost}game/create`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: difficulty ? difficulties[difficulty] : difficulties.hard
    });
    const body = await response.json();
    session = body.id;
    imageUrl = `${apiHost}${body.mapUrl.substring(1)}`;
}

function initButtons() {
    document.getElementById('level-zero').onclick = async () => {
        await createGame('zero');
        imageTag.src = imageUrl;
        turn = 0;
        document.getElementById('counter').innerText = `Turn ${turn++}`;
        document.getElementById('session').innerText = `Session: ${session}`;
    }
    document.getElementById('level-easy').onclick = async () => {
        await createGame('easy');
        imageTag.src = imageUrl;
        turn = 0;
        document.getElementById('counter').innerText = `Turn ${turn++}`;
        document.getElementById('session').innerText = `Session: ${session}`;
    }
    document.getElementById('level-medium').onclick = async () => {
        await createGame('medium');
        imageTag.src = imageUrl;
        turn = 0;
        document.getElementById('counter').innerText = `Turn ${turn++}`;
        document.getElementById('session').innerText = `Session: ${session}`;
    }
    document.getElementById('level-hard').onclick = async () => {
        await createGame('hard');
        imageTag.src = imageUrl;
        turn = 0;
        document.getElementById('counter').innerText = `Turn ${turn++}`;
        document.getElementById('session').innerText = `Session: ${session}`;
    }
    document.getElementById('level-brutal').onclick = async () => {
        await createGame('brutal');
        imageTag.src = imageUrl;
        turn = 0;
        document.getElementById('counter').innerText = `Turn ${turn++}`;
        document.getElementById('session').innerText = `Session: ${session}`;
    }
    document.getElementById('level-cave').onclick = async () => {
        await createGame('cave');
        imageTag.src = imageUrl;
        turn = 0;
        document.getElementById('counter').innerText = `Turn ${turn++}`;
        document.getElementById('session').innerText = `Session: ${session}`;
    }
    imageTag.onclick = async event => {
        const width = imageTag.offsetWidth;
        const height = imageTag.offsetHeight;
        const x = event.offsetX;
        const y = event.offsetY;

        if (x > y) {
            if (x < width - y * width / height) {
                move('up');
            } else {
                move('right');
            }
        } else {
            if (x < width - y * width / height) {
                move('left');
            } else {
                move('down');
            }
        }
    }
}

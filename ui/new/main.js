const SCALE = 1 // step 0.5

const BLOCK_SIZE = 40 * SCALE
const LINE_SIZE = 2 * SCALE // step 2
const HALF_LINE_SIZE = Math.floor(LINE_SIZE / 2)
const OUTER_BORDER_SIZE = 3 * SCALE
const PLAYER_SIZE = BLOCK_SIZE / 2

const API_HOST = 'http://localhost:8080';
// const API_HOST = 'https://labyrinth.edubovit.net/api'
const canvas = document.getElementById('canvas');
const ctx = canvas.getContext('2d');

let sessionId = undefined


function draw(map, userPos) {
    // ctx.fillStyle = "#000";
    // ctx.fillRect(0, 0, canvas.width, canvas.height);

    drawBlocks(map)
    drawPlayer(userPos)
    drawFieldBoard()
}

function drawFieldBoard() {
    ctx.fillStyle = "#000";
    ctx.fillRect(0, 0, canvas.width, OUTER_BORDER_SIZE + SCALE);
    ctx.fillRect(canvas.width - OUTER_BORDER_SIZE - SCALE, 0, OUTER_BORDER_SIZE + SCALE, canvas.height);
    ctx.fillRect(0, canvas.height - OUTER_BORDER_SIZE - SCALE, canvas.width, OUTER_BORDER_SIZE + SCALE);
    ctx.fillRect(0, 0, OUTER_BORDER_SIZE + SCALE, canvas.height);
}

function drawBlocks(map) {
    for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[i].length; j++) {
            drawBlock(i, j, map[i][j])
        }
    }
    for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[i].length; j++) {
            drawBlockBorder(i, j, map[i][j])
        }
    }
}

function drawBlock(i, j, item) {
    const newY = OUTER_BORDER_SIZE + i * BLOCK_SIZE
    const newX = OUTER_BORDER_SIZE + j * BLOCK_SIZE

    if (item.visibility === "REVEALED") {
        ctx.fillStyle = "#fff2cc";
    } else if (item.visibility === "HIDDEN") {
        ctx.fillStyle = "#bbb";
    } else if (item.visibility === "SEEN") {
        ctx.fillStyle = "#fff";
    }
    ctx.fillRect(newX, newY, BLOCK_SIZE, BLOCK_SIZE);
}

function drawBlockBorder(i, j, item) {
    const newY = OUTER_BORDER_SIZE + i * BLOCK_SIZE
    const newX = OUTER_BORDER_SIZE + j * BLOCK_SIZE
    if (item.visibility === "HIDDEN") {
        return;
    }
    ctx.fillStyle = "#000";
    if (item.wallUp) {
        ctx.fillRect(newX-SCALE, newY - HALF_LINE_SIZE, BLOCK_SIZE+2*SCALE, LINE_SIZE);
    }
    if (item.wallDown) {
        ctx.fillRect(newX-SCALE, newY + BLOCK_SIZE - LINE_SIZE + HALF_LINE_SIZE, BLOCK_SIZE+2*SCALE, LINE_SIZE);
    }
    if (item.wallLeft) {
        ctx.fillRect(newX - HALF_LINE_SIZE, newY-SCALE, LINE_SIZE, BLOCK_SIZE+2*SCALE);
    }
    if (item.wallRight) {
        ctx.fillRect(newX + BLOCK_SIZE - LINE_SIZE + HALF_LINE_SIZE, newY-SCALE, LINE_SIZE, BLOCK_SIZE+2*SCALE);
    }
}

function drawPlayer(userPos) {
    const deltaPlus = Math.floor((BLOCK_SIZE - PLAYER_SIZE) / 2)
    const newY = OUTER_BORDER_SIZE + userPos.x * BLOCK_SIZE + deltaPlus
    const newX = OUTER_BORDER_SIZE + userPos.y * BLOCK_SIZE + deltaPlus

    ctx.fillStyle = "#ff7700";
    ctx.fillRect(newX, newY, PLAYER_SIZE, PLAYER_SIZE);
}

window.onload = async () => {
    sessionId = new URLSearchParams(location.search).get("sessionId");
    if (!sessionId) {
        await createGame()
    } else {
        await getSessionGame()
    }

    const levels = document.getElementsByClassName('level')
    for (let i = 0; i < levels.length; i++) {
        levels[i].onclick = async function (event) {
            await createGame(event.target.dataset.value)
        }
    }
}

window.onkeydown = event => {
    switch (event.code) {
        case 'KeyW':
        case 'ArrowUp':
            doTheMove('up');
            event.preventDefault()
            break;
        case 'KeyA':
        case 'ArrowLeft':
            doTheMove('left');
            event.preventDefault()
            break;
        case 'KeyS':
        case 'ArrowDown':
            doTheMove('down');
            event.preventDefault()
            break;
        case 'KeyD':
        case 'ArrowRight':
            doTheMove('right');
            event.preventDefault()
            break;
        default:
    }
}


async function createGame(data) {
    if (!data) {
        data = '{"width":20,"height":20}'
    }
    const response = await fetch(`${API_HOST}/game/create/`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: data
    });
    const body = await response.json();
    let url = new URL(location.href);
    url.searchParams.set('sessionId', body.id);
    location.href = url.href
}

async function getSessionGame() {
    const response = await fetch(`${API_HOST}/game/${sessionId}/`, {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
    });
    const body = await response.json();
    canvas.height = OUTER_BORDER_SIZE * 2 + BLOCK_SIZE * body.map[0].length;
    canvas.width = OUTER_BORDER_SIZE * 2 + BLOCK_SIZE * body.map.length;

    draw(body.map, {x: body.playerCoordinates.i, y: body.playerCoordinates.j})
}

async function doTheMove(direction) {
    const response = await fetch(`${API_HOST}/game/${sessionId}/${direction}/`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
    });
    const body = await response.json();
    draw(body.map, {x: body.playerCoordinates.i, y: body.playerCoordinates.j})

    if (body.finish) {
        alert("Молодец какой")
    }
}


// function resize() {
//     $("#canvas").outerHeight($(window).height() - $("#canvas").offset().top - Math.abs($("#canvas").outerHeight(true) - $("#canvas").outerHeight()));
// }
//
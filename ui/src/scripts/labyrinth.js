const SCALE = 1// step 0.5

const BLOCK_SIZE = 40 * SCALE
const LINE_SIZE = 2 * SCALE // step 2
const HALF_LINE_SIZE = Math.floor(LINE_SIZE / 2)
const OUTER_BORDER_SIZE = 3 * SCALE
const PLAYER_SIZE = BLOCK_SIZE / 2

const REVEALED_COLOR = "#bbb"
const HIDDEN_COLOR = "#fff2cc"
const SEEN_COLOR = "#fff"
const BORDER_COLOR = "#000"
const PLAYER_COLOR = "#ff7700"

const API_HOST = process.env.API_HOST;
const canvas = document.getElementById('canvas');
const ctx = canvas.getContext('2d');

let sessionId = undefined
let playerCoordinates = {'x': 0, 'y': 0}

function draw(map, userPos) {
    // ctx.fillStyle = "#000";
    // ctx.fillRect(0, 0, canvas.width, canvas.height);

    drawBlocks(map)
    drawPlayer(userPos)
    drawFieldBoard()
}

function drawFieldBoard() {
    ctx.fillStyle = BORDER_COLOR;
    ctx.fillRect(0, 0, canvas.width, OUTER_BORDER_SIZE + SCALE);
    ctx.fillRect(canvas.width - OUTER_BORDER_SIZE - SCALE, 0, OUTER_BORDER_SIZE + SCALE, canvas.height);
    ctx.fillRect(0, canvas.height - OUTER_BORDER_SIZE - SCALE, canvas.width, OUTER_BORDER_SIZE + SCALE);
    ctx.fillRect(0, 0, OUTER_BORDER_SIZE + SCALE, canvas.height);
}

function drawBlocks(map) {
    for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[i].length; j++) {
            drawBlock(j, i, map[i][j])
        }
    }
    for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[i].length; j++) {
            drawBlockBorder(j, i, map[i][j])
        }
    }
}

function drawBlock(i, j, item) {
    const newX = OUTER_BORDER_SIZE + i * BLOCK_SIZE
    const newY = OUTER_BORDER_SIZE + j * BLOCK_SIZE

    if (item.visibility === "REVEALED") {
        ctx.fillStyle = REVEALED_COLOR;
    } else if (item.visibility === "HIDDEN") {
        ctx.fillStyle = HIDDEN_COLOR;
    } else if (item.visibility === "SEEN") {
        ctx.fillStyle = SEEN_COLOR;
    }
    ctx.fillRect(newX, newY, BLOCK_SIZE, BLOCK_SIZE);
}

function drawBlockBorder(i, j, item) {
    const newX = OUTER_BORDER_SIZE + i * BLOCK_SIZE
    const newY = OUTER_BORDER_SIZE + j * BLOCK_SIZE
    if (item.visibility === "HIDDEN") {
        return;
    }
    ctx.fillStyle = BORDER_COLOR;
    if (item.wallUp) {
        ctx.fillRect(newX - SCALE, newY - HALF_LINE_SIZE, BLOCK_SIZE + 2 * SCALE, LINE_SIZE);
    }
    if (item.wallDown) {
        ctx.fillRect(newX - SCALE, newY + BLOCK_SIZE - LINE_SIZE + HALF_LINE_SIZE, BLOCK_SIZE + 2 * SCALE, LINE_SIZE);
    }
    if (item.wallLeft) {
        ctx.fillRect(newX - HALF_LINE_SIZE, newY - SCALE, LINE_SIZE, BLOCK_SIZE + 2 * SCALE);
    }
    if (item.wallRight) {
        ctx.fillRect(newX + BLOCK_SIZE - LINE_SIZE + HALF_LINE_SIZE, newY - SCALE, LINE_SIZE, BLOCK_SIZE + 2 * SCALE);
    }
}

function drawPlayer(userPos) {
    const deltaPlus = Math.floor((BLOCK_SIZE - PLAYER_SIZE) / 2)
    const newY = OUTER_BORDER_SIZE + userPos.x * BLOCK_SIZE + deltaPlus
    const newX = OUTER_BORDER_SIZE + userPos.y * BLOCK_SIZE + deltaPlus

    ctx.fillStyle = PLAYER_COLOR;
    ctx.fillRect(newX, newY, PLAYER_SIZE, PLAYER_SIZE);
    playerCoordinates.x = newX
    playerCoordinates.y = newY
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
    setSessionId(body.id)
}

async function getSessionGame() {
    const response = await fetch(`${API_HOST}/game/${sessionId}/`, {
        method: 'GET',
        headers: {'Content-Type': 'application/json'},
    });
    const body = await response.json();
    canvas.width = OUTER_BORDER_SIZE * 2 + BLOCK_SIZE * body.map[0].length;
    canvas.height = OUTER_BORDER_SIZE * 2 + BLOCK_SIZE * body.map.length;
    document.getElementsByClassName("moves__count")[0].innerHTML = body.turns
    draw(body.map, {x: body.playerCoordinates.i, y: body.playerCoordinates.j})
}

async function doTheMove(direction) {
    const response = await fetch(`${API_HOST}/game/${sessionId}/${direction}/`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
    });
    const body = await response.json();
    draw(body.map, {x: body.playerCoordinates.i, y: body.playerCoordinates.j})
    document.getElementsByClassName("moves__count")[0].innerHTML = body.turns
    if (body.finish) {
        alert("Молодец какой")
    }
}


window.onload = async () => {
    await setEvents()
    await index()
}

async function index() {
    sessionId = getSessionId()
    if (!sessionId) {
        await createGame()
    } else {
        await getSessionGame()
    }

}

function getSessionId() {
    return new URLSearchParams(location.search).get("sessionId")
}

function setSessionId(new_id) {
    let url = new URL(location.href);
    url.searchParams.set('sessionId', new_id);
    location.href = url.href
}

function setEvents() {
    // kb move
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

    // click move
    canvas.onclick = async event => {
        const offsetX = event.offsetX - playerCoordinates.x;
        const offsetY = event.offsetY - playerCoordinates.y;
        if (Math.abs(offsetY) > Math.abs(offsetX)) {
            if (offsetY < 0) {
                doTheMove('up');
            } else {
                doTheMove('down');
            }
        } else {
            if (offsetX < 0) {
                doTheMove('left');
            } else {
                doTheMove('right');
            }
        }
    }

    // level buttons click
    const levels = document.getElementsByClassName('level')
    for (let i = 0; i < levels.length; i++) {
        levels[i].onclick = async function (event) {
            await createGame(event.target.dataset.value)
        }
    }

}
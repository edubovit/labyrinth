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
const ENTER_COLOR = "#f00"
const EXIT_COLOR = "#0f0"
const PLAYER_COLOR = "#ff7700"

const API_HOST = process.env.API_HOST;
const canvas = document.getElementById('canvas');
const ctx = canvas.getContext('2d');

let currentPage = 'login';

let playerCoordinates = {'x': 0, 'y': 0}


window.onload = async () => {
    await setEvents()
    await index()
}


async function index() {
    if (await checkSession()) {
        await loadGame();
    }
}

async function checkSession() {
    const response = await fetch(`${API_HOST}/user/me`, {
        method: "GET",
        credentials: "include"
    });
    if (response.ok) {
        showPageGame();
    } else {
        showPageLogin();
    }
    return response.ok;
}

async function loadGame() {
    if (!await getSessionGame()) {
        await createGame();
    }
}

async function getSessionGame() {
    const response = await fetch(`${API_HOST}/game/`, {
        method: 'GET',
        credentials: "include",
    });
    if (!response.ok) {
        return false;
    }
    renderGame(await response.json());
    return true;
}

async function createGame(data) {
    if (!data) {
        data = '{"width":20,"height":20}'
    }
    const response = await fetch(`${API_HOST}/game/create/`, {
        method: 'POST',
        credentials: "include",
        headers: {'Content-Type': 'application/json'},
        body: data
    });
    renderGame(await response.json());
}

async function doTheMove(direction) {
    const response = await fetch(`${API_HOST}/game/${direction}/`, {
        method: 'POST',
        credentials: "include",
        headers: {'Content-Type': 'application/json'},
    });
    const body = await response.json();
    reflectChanges(body.changes, {x: body.playerCoordinates.j, y: body.playerCoordinates.i});
    document.getElementsByClassName("moves__count")[0].innerHTML = body.turns;
    if (body.finish) {
        alert("Молодец какой");
    }
}

function renderGame(game) {
    canvas.width = OUTER_BORDER_SIZE * 2 + BLOCK_SIZE * game.map[0].length;
    canvas.height = OUTER_BORDER_SIZE * 2 + BLOCK_SIZE * game.map.length;
    document.getElementsByClassName("moves__count")[0].innerHTML = game.turns;
    draw(game.map, {x: game.playerCoordinates.j, y: game.playerCoordinates.i});
}

function draw(map, userPos) {
    drawAllTiles(map);
    drawPlayer(userPos);
    drawOuterBorders();
}

function reflectChanges(changedTiles, userPos) {
    changedTiles.forEach(tile => {
        drawTile(tile.i, tile.j, tile.newState);
        drawTileBorder(tile.i, tile.j, tile.newState);
    });
    drawPlayer(userPos);
    drawOuterBorders();
}

function drawAllTiles(map) {
    for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[i].length; j++) {
            drawTile(i, j, map[i][j])
        }
    }
    for (let i = 0; i < map.length; i++) {
        for (let j = 0; j < map[i].length; j++) {
            drawTileBorder(i, j, map[i][j])
        }
    }
}

function drawTile(i, j, state) {
    const newX = OUTER_BORDER_SIZE + j * BLOCK_SIZE;
    const newY = OUTER_BORDER_SIZE + i * BLOCK_SIZE;

    if (state.visibility === "REVEALED") {
        ctx.fillStyle = REVEALED_COLOR;
    } else if (state.visibility === "HIDDEN") {
        ctx.fillStyle = HIDDEN_COLOR;
    } else if (state.visibility === "SEEN") {
        ctx.fillStyle = SEEN_COLOR;
    }
    ctx.fillRect(newX, newY, BLOCK_SIZE, BLOCK_SIZE);
}

function drawTileBorder(i, j, state) {
    const newX = OUTER_BORDER_SIZE + j * BLOCK_SIZE;
    const newY = OUTER_BORDER_SIZE + i * BLOCK_SIZE;
    if (state.visibility === "HIDDEN") {
        return;
    }
    ctx.fillStyle = BORDER_COLOR;
    if (state.wallUp) {
        ctx.fillRect(newX - SCALE, newY - HALF_LINE_SIZE, BLOCK_SIZE + 2 * SCALE, LINE_SIZE);
    }
    if (state.wallDown) {
        ctx.fillRect(newX - SCALE, newY + BLOCK_SIZE - LINE_SIZE + HALF_LINE_SIZE, BLOCK_SIZE + 2 * SCALE, LINE_SIZE);
    }
    if (state.wallLeft) {
        ctx.fillRect(newX - HALF_LINE_SIZE, newY - SCALE, LINE_SIZE, BLOCK_SIZE + 2 * SCALE);
    }
    if (state.wallRight) {
        ctx.fillRect(newX + BLOCK_SIZE - LINE_SIZE + HALF_LINE_SIZE, newY - SCALE, LINE_SIZE, BLOCK_SIZE + 2 * SCALE);
    }
}

function drawPlayer(userPos) {
    const deltaPlus = Math.floor((BLOCK_SIZE - PLAYER_SIZE) / 2);
    const newX = OUTER_BORDER_SIZE + userPos.x * BLOCK_SIZE + deltaPlus;
    const newY = OUTER_BORDER_SIZE + userPos.y * BLOCK_SIZE + deltaPlus;

    ctx.fillStyle = PLAYER_COLOR;
    ctx.fillRect(newX, newY, PLAYER_SIZE, PLAYER_SIZE);
    playerCoordinates.x = newX;
    playerCoordinates.y = newY;
}

function drawOuterBorders() {
    ctx.fillStyle = BORDER_COLOR;
    ctx.fillRect(0, 0, canvas.width, OUTER_BORDER_SIZE + SCALE);
    ctx.fillRect(canvas.width - OUTER_BORDER_SIZE - SCALE, 0, OUTER_BORDER_SIZE + SCALE, canvas.height);
    ctx.fillRect(0, canvas.height - OUTER_BORDER_SIZE - SCALE, canvas.width, OUTER_BORDER_SIZE + SCALE);
    ctx.fillRect(0, 0, OUTER_BORDER_SIZE + SCALE, canvas.height);
    ctx.fillStyle = EXIT_COLOR;
    ctx.fillRect(OUTER_BORDER_SIZE + SCALE, 0, BLOCK_SIZE, OUTER_BORDER_SIZE + LINE_SIZE);
    ctx.fillStyle = ENTER_COLOR;
    ctx.fillRect(canvas.width - OUTER_BORDER_SIZE - SCALE - BLOCK_SIZE, canvas.height - OUTER_BORDER_SIZE - SCALE,
        BLOCK_SIZE, OUTER_BORDER_SIZE + LINE_SIZE);
}

function setEvents() {
    // kb move
    window.onkeydown = event => {
        if (currentPage !== 'game') {
            return;
        }
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
    if(detectMobile()) {
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
    }

    // level buttons click
    const levels = document.getElementsByClassName('level')
    for (let i = 0; i < levels.length; i++) {
        levels[i].onclick = async function (event) {
            await createGame(event.target.dataset.value)
        }
    }

    document.getElementById('login-button').onclick = authenticate('login', 'Login failed');
    document.getElementById('signup-button').onclick = authenticate('signup', 'Registration failed');
    document.getElementById('logout-button').onclick = async event => {
        const response = await fetch(`${API_HOST}/user/logout`, {
            method: "POST",
            credentials: "include"
        });
        if (response.ok) {
            showPageLogin();
        }
    }
}

function authenticate(path, errorMessage) {
    return async () => {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const response = await fetch(`${API_HOST}/user/${path}`, {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/json'},
            body: `{"username":"${username}","password":"${password}"}`,
        });
        if (response.ok) {
            await loadGame();
            showPageGame();
        } else {
            document.getElementById('login-message').innerText = errorMessage;
        }
    }
}

function showPageLogin() {
    currentPage = 'login';
    document.getElementById('page-login').style.display = 'block';
    document.getElementById('page-game').style.display = 'none';
}

function showPageGame() {
    currentPage = 'game';
    document.getElementById('login-message').innerText = '';
    document.getElementById('page-login').style.display = 'none';
    document.getElementById('page-game').style.display = 'block';
}

function detectMobile() {
    const toMatch = [
        /Android/i,
        /webOS/i,
        /iPhone/i,
        /iPad/i,
        /iPod/i,
        /BlackBerry/i,
        /Windows Phone/i
    ];

    return toMatch.some((toMatchItem) => {
        return navigator.userAgent.match(toMatchItem);
    });
}

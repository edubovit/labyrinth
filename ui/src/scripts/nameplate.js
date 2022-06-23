export class Nameplate {

    constructor(player) {
        this.player = player;
    }

    updatePosition = () => {
        if (!this.nameplate) {
            this.create();
        }
        const container = document.getElementById('labyrinth');
        this.nameplate.style.left = (container.offsetLeft + this.player.x) + 'px';
        this.nameplate.style.top = (container.offsetTop + this.player.y) + 'px';
    }

    create = () => {
        const container = document.getElementById('labyrinth');
        const nameplate = document.createElement('div');
        this.nameplate = nameplate;
        nameplate.id = `nameplate-${this.player.username}`;
        nameplate.className = 'nameplate';
        nameplate.innerText = this.player.username;
        container.append(nameplate);
    }

    remove = () => {
        this.nameplate.remove();
    }

}
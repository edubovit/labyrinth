import {Nameplate} from "./nameplate";

export class Player {
    x = 0;
    y = 0;

    constructor(username) {
        this.username = username;
        this.nameplate = new Nameplate(this);
    }
}

import {Nameplate} from "./Nameplate";
import {PlayerColorPicker} from "./PlayerColorPicker";

export class Player {

    color = PlayerColorPicker.pickColor();
    x = 0;
    y = 0;

    constructor(username) {
        this.username = username;
        this.nameplate = new Nameplate(this);
    }

}

export class PlayerColorPicker {

    static colors = [
        '#f70',
        '#00f',
        '#f00',
        '#070',
        '#f0f',
        '#0ff',
        '#000',
        '#700',
        '#777',
        '#faf',
        '#0f0',
        '#007',
        '#f77',
        '#afa',
        '#7c7',
        '#77f',
        '#ccc',
    ];

    static currentColorIdx = 0;

    static pickColor = () => {
        const color = this.colors[this.currentColorIdx];
        this.currentColorIdx = (this.currentColorIdx + 1) % this.colors.length;
        return color;
    }

    static reset = () => {
        this.currentColorIdx = 0;
    }

}

<template>
    <div :style="`height: ${size}px; width: ${size}px;`"
         v-html="emoji"></div>
</template>

<script>
    import {EmojiIndex} from "emoji-mart-vue-fast"
    import data from "emoji-mart-vue-fast/data/all.json";

    const emojiIndex = new EmojiIndex(data);

    export default {
        props: ['value', 'size'],
        data: () => ({
            messengerSheet: require('@/assets/messenger-emoji-sheet-64.png')
        }),
        computed: {
            emoji() {
                let emoji = emojiIndex.findEmoji(this.value);

                let style = `
                    background-position: ${emoji.getPosition()};
                    background-image: url(${this.messengerSheet});
                    background-size: 5200%;
                    width: ${this.size}px; height: ${this.size}px;
                    display: inline-block;
                `;

                return `<div class='emoji' style="${style}"></div>`;
            }
        }

    }
</script>
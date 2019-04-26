<template>
    <div style="height: 24px; width: 24px;"
         v-html="emoji"></div>
</template>

<script>
    import {EmojiIndex} from "emoji-mart-vue-fast"
    import data from "emoji-mart-vue-fast/data/all.json";

    let emojiIndex = new EmojiIndex(data);

    export default {
        props: ['value'],
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
                    width: 24px; height: 24px;
                    display: inline-block;
                `;

                return `<div class='emoji' style="${style}"></div>`;
            }
        }

    }
</script>
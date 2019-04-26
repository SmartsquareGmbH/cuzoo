<template>
    <div v-html="emoji"/>
</template>

<script>
    import {EmojiIndex} from "emoji-mart-vue-fast"
    import data from "emoji-mart-vue-fast/data/all.json";

    let emojiIndex = new EmojiIndex(data);

    export default {
        props: ['value'],
        data: () => ({
            emojiIndex: emojiIndex,
            messengerSheet: require('@/assets/messenger-emoji-sheet-64.png')
        }),
        computed: {
            emoji() {
                let emoji = emojiIndex.findEmoji(this.value);

                let style = `background-position: ${emoji.getPosition()}; background-image: url(${this.messengerSheet}); width: 24px; height: 24px; display: inline-block; background-size: 5200%`;

                return `<div class='emoji' style="${style}"></div>`;
            }
        }

    }
</script>
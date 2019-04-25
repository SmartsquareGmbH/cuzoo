<template>
    <nimble-picker @select="passEmoji"
                   set="messenger"
                   :data="emojiSet"
                   :i18n="i18n"/>
</template>

<script>
    import {NimblePicker} from "emoji-mart-vue-fast";
    import messenger from 'emoji-mart-vue-fast/data/messenger.json'

    export default {
        components: {
            NimblePicker
        },
        data: () => ({
            emojiSet: messenger,
            loading: true,
            i18n: {
                search: 'Suche...',
                notfound: 'Nichts gefunden...',
                categories: {
                    search: 'Suchergebnisse',
                    recent: 'Zuletzt verwendet',
                    people: 'Leute',
                    nature: 'Tiere & Natur',
                    foods: 'Essen & Trinken',
                    activity: 'Aktivität',
                    places: 'Reisen & Orte',
                    objects: 'Objeke',
                    symbols: 'Symbole',
                    flags: 'Flaggen',
                    custom: 'Benutzerdefiniert',
                }
            }
        }),
        methods: {
            passEmoji(emoji) {
                this.$emit("emoji-chosen", emoji);
            }
        }
    }
</script>
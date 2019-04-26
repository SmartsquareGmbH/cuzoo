<template>
    <picker @select="passEmoji"
            set="messenger"
            :i18n="i18n"/>
</template>

<script>
    import { Picker } from "emoji-mart-vue-fast"

    export default {
        components: {
            Picker
        },
        data: () => ({
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
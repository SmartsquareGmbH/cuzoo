<template>
    <picker set="apple"
            title="Kontaktpunkt bewerten"
            emoji="speech_balloon"
            :i18n="i18n"
            @select="passEmoji"/>
</template>

<script>
    import {Picker} from "emoji-mart-vue"

    export default {
        components: {
            Picker
        },
        data: () => ({
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
                    objects: 'Objekte',
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
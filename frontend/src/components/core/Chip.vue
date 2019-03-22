<template>
    <v-chip :class="`subheading ${fontColor}--text my-1`"
            small>
        <slot/>
    </v-chip>
</template>

<script>
    export default {
        props: ['fontColor']
    }
</script>
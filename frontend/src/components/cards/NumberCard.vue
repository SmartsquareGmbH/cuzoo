<template>
    <v-card class="pl-3 mt-2"
            elevation="6"
            height="75">
        <v-layout row wrap>
            <v-flex xs6>
                <v-sheet
                        elevation="12"
                        class="v-sheet--offset text-xs-center"
                        height="75"
                        width="100"
                        :color="color">
                    <span class="display-3 font-weight-light">
                        {{ number }}
                    </span>
                </v-sheet>
            </v-flex>
            <v-flex xs6 class="more-padding-top">
                <span class="headline font-weight-light">
                    {{ title }}
                </span>
            </v-flex>
        </v-layout>
    </v-card>
</template>

<script>
    export default {
        props: ['number', 'title', 'color']
    }
</script>

<style scoped>
    .v-sheet--offset {
        top: -24px;
        position: relative;
    }

    .more-padding-top {
        padding-top: 20px !important;
    }
</style>
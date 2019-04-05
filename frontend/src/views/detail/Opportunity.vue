<template>
    <v-container>
        <v-layout row wrap text-xs-center>
            <v-flex xs1>
                <v-btn flat small @click="$router.go(-1)">
                    <v-icon size="22px" class="mr-1" dark>arrow_back</v-icon>
                    Zurück
                </v-btn>
            </v-flex>
            <v-flex xs10>
                <span>{{ opportunityId }}</span>
            </v-flex>
        </v-layout>
    </v-container>
</template>
<script>
    export default {
        data: () => ({
        }),
        computed: {
            opportunityId() {
                return this.$route.params.opportunityId;
            }
        }
    }
</script>
<template>
    <v-combobox
            v-model="labels"
            :items="responseLabels"
            :search-input.sync="labelBoxInput"
            @change="resetLabels()"
            prepend-icon="label"
            color="primary"
            :label="type"
            outline
            clearable
            multiple
            solo
            hide-details>
        <template slot="selection" slot-scope="label" tabindex="-1">
            <v-chip tabindex="-1"
                    class="title"
                    :selected="label.selected"
                    close
                    @input="removeLabel(label.item)">
                {{ label.item }}
            </v-chip>
        </template>
        <template slot="no-data" v-if="labelBoxInput" tabindex="-1">
            <v-list-tile v-if="labelBoxInput.replace(/ /g, '') !== ''">
                <v-list-tile-content>
                    <v-list-tile-title>
                        Keine Labels für
                        "<strong class="primary--text">{{ labelBoxInput }}</strong>"
                        gefunden. Drücke <kbd>Enter</kbd> um es zu erstellen.
                    </v-list-tile-title>
                </v-list-tile-content>
            </v-list-tile>
        </template>
    </v-combobox>
</template>

<script>
    import api from '../../../utils/http-common';
    import debounce from 'lodash.debounce';

    const debouncedLabelApiCall = debounce(getLabelsByInput, 150, {leading: true});

    export default {
        props: ['apiPath', 'type', 'currentLabels'],
        data: () => ({
            labels: [],
            responseLabels: [],
            labelBoxInput: ''
        }),
        computed: {
            temporaryLabels() {
                return this.labels;
            }
        },
        watch: {
            currentLabels() {
                this.labels = this.currentLabels;
            },
            labelBoxInput(input) {
                if (input && removeNonLetters(input) !== '') {
                    let call = debouncedLabelApiCall(this.apiPath, input);

                    if (call) {
                        call.then(res => this.responseLabels = res);
                    }

                    this.responseLabels.forEach(label => {
                        if (removeNonLetters(label) === removeNonLetters(input)) {
                            this.labelBoxInput = label;
                        }
                    });
                }
            },
            temporaryLabels() {
                this.labels
                    .map(it => removeNonLetters(it))
                    .map(it => {
                        if (it === '') this.removeLabel(it)
                    });
            }
        },
        methods: {
            resetLabels() {
                this.passCurrentLabels();

                this.labelBoxInput = '';
                this.responseLabels = [];
            },
            passCurrentLabels() {
                this.$emit('label-added', this.labels);
            },
            removeLabel(item) {
                this.labels.splice(this.labels.indexOf(item), 1);
                this.labels = [...this.labels]
            },
        }
    }

    function getLabelsByInput(apiPath, input) {
        return api.get(`${apiPath}/get/labels/${removeNonLetters(input)}`)
            .then(res => {
                return res.data;
            });
    }

    function removeNonLetters(string) {
        return string.replace(/-/g, '').replace(/ /g, '').toLowerCase();
    }
</script>
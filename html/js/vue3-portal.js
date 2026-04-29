(function () {
    if (!window.WebPortalItemImageHelper) {
        const MC_ASSET_VERSION = '1.21.5';
        const ITEM_TEXTURE_BASE = `https://assets.mcasset.cloud/${MC_ASSET_VERSION}/assets/minecraft/textures/item/`;
        const BLOCK_TEXTURE_BASE = `https://assets.mcasset.cloud/${MC_ASSET_VERSION}/assets/minecraft/textures/block/`;
        const LOCAL_IMAGE_PATTERN = /(^|\/)(images)\/(.+)\.(png|gif|jpg|jpeg|webp)$/i;
        const ITEM_ALIASES = {
            grilled_pork: 'cooked_porkchop',
            pork: 'porkchop'
        };

        function normalizeTextureName(rawName) {
            const withoutExtension = (rawName || '')
                .replace(/\.[^.]+$/, '')
                .replace(/%20/g, ' ')
                .trim();

            const snakeCaseName = withoutExtension
                .replace(/([A-Z]+)([A-Z][a-z])/g, '$1_$2')
                .replace(/([a-z0-9])([A-Z])/g, '$1_$2')
                .replace(/[\s\-]+/g, '_')
                .replace(/[^a-zA-Z0-9_]/g, '_')
                .replace(/_+/g, '_')
                .replace(/^_+|_+$/g, '')
                .toLowerCase();

            return ITEM_ALIASES[snakeCaseName] || snakeCaseName;
        }

        function toAssetSources(imagePath) {
            const match = (imagePath || '').match(LOCAL_IMAGE_PATTERN);
            if (!match) {
                return null;
            }

            const textureName = normalizeTextureName(match[3]);
            if (!textureName) {
                return null;
            }

            return {
                item: `${ITEM_TEXTURE_BASE}${textureName}.png`,
                block: `${BLOCK_TEXTURE_BASE}${textureName}.png`,
                original: imagePath
            };
        }

        function upgradeHtml(html) {
            if (typeof html !== 'string' || html.indexOf('<img') === -1 || html.indexOf('images/') === -1) {
                return html;
            }

            const template = document.createElement('template');
            template.innerHTML = html;
            const images = template.content.querySelectorAll('img[src]');

            images.forEach(img => {
                const sources = toAssetSources(img.getAttribute('src'));
                if (!sources) {
                    return;
                }

                img.setAttribute('src', sources.item);
                img.setAttribute('data-wp-item-src', sources.item);
                img.setAttribute('data-wp-block-src', sources.block);
                img.setAttribute('data-wp-original-src', sources.original);
            });

            return template.innerHTML;
        }

        document.addEventListener('error', event => {
            const target = event.target;
            if (!(target instanceof HTMLImageElement)) {
                return;
            }

            const currentSrc = target.getAttribute('src');
            const itemSrc = target.getAttribute('data-wp-item-src');
            const blockSrc = target.getAttribute('data-wp-block-src');
            const originalSrc = target.getAttribute('data-wp-original-src');

            if (itemSrc && currentSrc === itemSrc && blockSrc) {
                target.setAttribute('src', blockSrc);
                return;
            }

            if (blockSrc && currentSrc === blockSrc && originalSrc && originalSrc !== blockSrc) {
                target.setAttribute('src', originalSrc);
                return;
            }

            target.removeAttribute('data-wp-item-src');
            target.removeAttribute('data-wp-block-src');
            target.removeAttribute('data-wp-original-src');
        }, true);

        window.WebPortalItemImageHelper = {
            upgradeHtml
        };
    }

    const colorMap = {
        primary: '#1f4d7a',
        secondary: '#b7791f',
        'green darken-1': '#2e7d32',
        'red darken-1': '#c62828',
        'amber darken-2': '#ff8f00',
        'light-blue darken-1': '#039be5',
        'light-blue': '#03a9f4',
        'deep-orange': '#f4511e',
        lime: '#9e9d24',
        red: '#e53935',
        transparent: 'transparent'
    };

    function ensureMdi() {
        if (document.querySelector('link[data-webportal-mdi]')) {
            return;
        }

        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = 'https://cdn.jsdelivr.net/npm/@mdi/font@7.4.47/css/materialdesignicons.min.css';
        link.setAttribute('data-webportal-mdi', 'true');
        document.head.appendChild(link);
    }

    function buildUtilityCss() {
        const rules = [];
        const spacingProps = [
            ['m', 'margin'],
            ['p', 'padding'],
            ['mt', 'margin-top'],
            ['mb', 'margin-bottom'],
            ['ml', 'margin-left'],
            ['mr', 'margin-right'],
            ['mx', ['margin-left', 'margin-right']],
            ['my', ['margin-top', 'margin-bottom']],
            ['pt', 'padding-top'],
            ['pb', 'padding-bottom'],
            ['pl', 'padding-left'],
            ['pr', 'padding-right'],
            ['px', ['padding-left', 'padding-right']],
            ['py', ['padding-top', 'padding-bottom']]
        ];

        for (let step = 0; step <= 12; step += 1) {
            const value = `${step * 4}px`;
            spacingProps.forEach(([prefix, prop]) => {
                if (Array.isArray(prop)) {
                    rules.push(`.${prefix}-${step}{${prop[0]}:${value}!important;${prop[1]}:${value}!important;}`);
                } else {
                    rules.push(`.${prefix}-${step}{${prop}:${value}!important;}`);
                }
            });
        }

        const responsiveSpacing = [6, 8, 10, 12];
        responsiveSpacing.forEach((step) => {
            const value = `${step * 4}px`;
            rules.push(`@media (min-width: 960px){.pa-md-${step}{padding:${value}!important;}.py-md-${step}{padding-top:${value}!important;padding-bottom:${value}!important;}.mt-md-${step}{margin-top:${value}!important;}.mb-md-${step}{margin-bottom:${value}!important;}.pr-md-${step}{padding-right:${value}!important;}}`);
        });
        rules.push('@media (min-width: 960px){.mt-md-0{margin-top:0!important;}.mb-md-0{margin-bottom:0!important;}}');

        for (let i = 1; i <= 12; i += 1) {
            const pct = `${(i / 12) * 100}%`;
            rules.push(`.wp-col-${i}{flex:0 0 ${pct};max-width:${pct};}`);
            rules.push(`@media (min-width: 600px){.wp-sm-${i}{flex:0 0 ${pct};max-width:${pct};}}`);
            rules.push(`@media (min-width: 960px){.wp-md-${i}{flex:0 0 ${pct};max-width:${pct};}}`);
            rules.push(`@media (min-width: 1264px){.wp-lg-${i}{flex:0 0 ${pct};max-width:${pct};}}`);
        }

        return rules.join('');
    }

    function injectStyles() {
        if (document.getElementById('webportal-vue3-runtime-style')) {
            return;
        }

        const style = document.createElement('style');
        style.id = 'webportal-vue3-runtime-style';
        style.textContent = `
            * { box-sizing: border-box; }
            body { margin: 0; padding-top: 64px; color: #17324d; font-family: Segoe UI, Tahoma, Geneva, Verdana, sans-serif; }
            a { color: inherit; }
            img { max-width: 100%; display: block; }
            .wp-app { min-height: 100vh; }
            .wp-main { min-height: calc(100vh - 64px); }
            .wp-container { width: min(1180px, calc(100% - 32px)); margin: 0 auto; }
            .wp-container-fluid { width: calc(100% - 32px); max-width: none; margin: 0 auto; }
            .wp-row { display: flex; flex-wrap: wrap; margin: -8px; }
            .wp-row.wp-row-dense { margin: -4px; }
            .wp-row > .wp-col { padding: 8px; }
            .wp-row.wp-row-dense > .wp-col { padding: 4px; }
            .wp-col { flex: 0 0 100%; max-width: 100%; min-width: 0; }
            .wp-justify-center { justify-content: center; }
            .wp-justify-end { justify-content: flex-end; }
            .wp-justify-space-between { justify-content: space-between; }
            .wp-align-center { align-items: center; }
            .wp-app-bar { position: fixed; inset: 0 0 auto 0; z-index: 100; display: flex; align-items: center; gap: 12px; min-height: 64px; padding: 10px 20px; background: #1f4d7a; color: #fff; box-shadow: 0 10px 30px rgba(14, 23, 37, 0.18); }
            .wp-app-bar.is-app { position: fixed; inset: 0 0 auto 0; z-index: 100; }
            .wp-app-bar.is-dense { min-height: 56px; }
            .wp-toolbar-title { font-size: 1.15rem; font-weight: 700; }
            .wp-spacer { flex: 1 1 auto; }
            .wp-card, .wp-sheet, .wp-alert, .wp-data-table-wrap { border-radius: 18px; background: rgba(255,255,255,0.9); border: 1px solid rgba(23,50,77,0.1); box-shadow: 0 16px 40px rgba(25, 35, 46, 0.1); color: #17324d; }
            .wp-card.is-flat, .wp-sheet.is-flat { box-shadow: none; }
            .wp-card.is-outlined { box-shadow: none; }
            .wp-card-title, .wp-card-text, .wp-card-actions { padding: 20px; }
            .wp-card-title { font-size: 1.125rem; font-weight: 700; }
            .wp-card-text + .wp-card-actions, .wp-card-title + .wp-card-text { padding-top: 0; }
            .wp-card-actions { display: flex; align-items: center; gap: 12px; }
            .wp-footer { padding: 16px 20px; }
            .wp-footer.is-fixed { position: sticky; bottom: 0; left: 0; right: 0; background: rgba(255,255,255,0.92); backdrop-filter: blur(10px); color: #17324d; }
            .wp-list { display: flex; flex-direction: column; gap: 4px; padding: 4px 0; }
            .wp-list-item { display: flex; align-items: flex-start; gap: 12px; padding: 10px 12px; border-radius: 12px; }
            .wp-list-item:hover { background: rgba(23, 50, 77, 0.05); }
            .wp-list-item-content { flex: 1 1 auto; min-width: 0; }
            .wp-list-item-title { font-weight: 600; }
            .wp-list-item-subtitle { color: rgba(23,50,77,0.76); font-size: 0.94rem; margin-top: 4px; }
            .wp-avatar { display: inline-flex; align-items: center; justify-content: center; overflow: hidden; border-radius: 999px; background: rgba(23,50,77,0.08); }
            .wp-btn-group { display: inline-flex; flex-wrap: wrap; gap: 8px; }
            .wp-btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; min-height: 40px; padding: 0 18px; border-radius: 12px; border: 1px solid transparent; background: #1f4d7a; color: #fff; font: inherit; font-weight: 600; text-decoration: none; cursor: pointer; transition: transform .15s ease, box-shadow .15s ease, background-color .15s ease; box-shadow: 0 10px 20px rgba(31,77,122,0.18); }
            .wp-btn:hover { transform: translateY(-1px); }
            .wp-btn:disabled { opacity: .6; cursor: not-allowed; transform: none; }
            .wp-btn.is-text { background: transparent; color: inherit; box-shadow: none; border-color: transparent; }
            .wp-btn.is-outlined { background: transparent; color: inherit; border-color: currentColor; box-shadow: none; }
            .wp-btn.is-block { display: flex; width: 100%; }
            .wp-btn.is-large { min-height: 48px; padding: 0 22px; }
            .wp-btn.is-icon { width: 40px; min-width: 40px; padding: 0; }
            .wp-icon { display: inline-flex; align-items: center; justify-content: center; font-size: 1.1rem; line-height: 1; }
            .wp-icon.is-right { margin-left: 4px; }
            .wp-menu { position: relative; display: inline-flex; }
            .wp-menu-content { position: absolute; top: calc(100% + 8px); right: 0; min-width: 220px; padding: 8px; background: rgba(255,255,255,0.98); border: 1px solid rgba(23,50,77,0.12); border-radius: 16px; box-shadow: 0 20px 40px rgba(20, 31, 44, 0.16); z-index: 50; color: #17324d; }
            .wp-field { display: flex; flex-direction: column; gap: 8px; width: 100%; }
            .wp-field + .wp-field { margin-top: 12px; }
            .wp-field-label { font-size: 0.92rem; font-weight: 600; color: rgba(23,50,77,0.8); }
            .wp-field-input-wrap { display: flex; align-items: center; gap: 10px; width: 100%; padding: 0 14px; min-height: 46px; border-radius: 14px; border: 1px solid rgba(23,50,77,0.18); background: rgba(255,255,255,0.98); color: #17324d; }
            .wp-field-input-wrap.is-outlined { box-shadow: inset 0 0 0 1px rgba(23,50,77,0.04); }
            .wp-field-input { width: 100%; border: 0; outline: none; background: transparent; color: inherit; font: inherit; min-height: 42px; }
            .wp-select { width: 100%; border: 0; outline: none; background: transparent; font: inherit; color: inherit; min-height: 42px; }
            .wp-alert { padding: 12px 16px; }
            .wp-alert.is-info { border-color: rgba(3,155,229,0.22); background: rgba(3,155,229,0.08); }
            .wp-alert.is-error { border-color: rgba(198,40,40,0.24); background: rgba(198,40,40,0.08); }
            .wp-dialog-overlay { position: fixed; inset: 0; z-index: 100; display: flex; align-items: center; justify-content: center; padding: 24px; background: rgba(15, 23, 35, 0.45); }
            .wp-dialog-card { width: min(100%, 640px); max-height: calc(100vh - 48px); overflow: auto; }
            .wp-data-table-wrap { overflow: auto; }
            .wp-data-table { width: 100%; border-collapse: collapse; }
            .wp-data-table th, .wp-data-table td { padding: 14px 16px; text-align: left; border-bottom: 1px solid rgba(23,50,77,0.08); vertical-align: top; }
            .wp-data-table th { font-size: 0.8rem; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: rgba(23,50,77,0.62); }
            .wp-data-table tbody tr:hover { background: rgba(23,50,77,0.04); }
            .wp-data-table-empty { padding: 20px; color: rgba(23,50,77,0.7); }
            .d-flex { display: flex !important; }
            .flex-wrap { flex-wrap: wrap !important; }
            .flex-grow-1 { flex-grow: 1 !important; }
            .justify-center { justify-content: center !important; }
            .justify-space-between { justify-content: space-between !important; }
            .justify-md-end { justify-content: flex-end !important; }
            .align-center { align-items: center !important; }
            .text-center { text-align: center !important; }
            .text-uppercase { text-transform: uppercase !important; }
            .text-caption { font-size: .8rem !important; }
            .text-h4 { font-size: 2rem !important; font-weight: 800 !important; }
            .text-h5 { font-size: 1.5rem !important; font-weight: 700 !important; }
            .text-h6 { font-size: 1.2rem !important; font-weight: 700 !important; }
            .headline { font-size: 1.25rem !important; font-weight: 700 !important; }
            .font-weight-bold { font-weight: 700 !important; }
            .font-weight-medium { font-weight: 500 !important; }
            .grey--text, .text--darken-1 { color: rgba(23,50,77,0.74) !important; }
            .elevation-0 { box-shadow: none !important; }
            .elevation-1 { box-shadow: 0 16px 36px rgba(25, 35, 46, 0.12) !important; }
            .transparent { background: transparent !important; }
            .fill-height { height: 100%; }
            pre { white-space: pre-wrap; word-break: break-word; }
            ${buildUtilityCss()}
        `;
        document.head.appendChild(style);
    }

    function getColor(color) {
        return colorMap[color] || color;
    }

    function getSlotText(slots) {
        const nodes = slots && slots.default ? slots.default() : [];
        return flattenText(nodes).trim();
    }

    function flattenText(nodes) {
        return (nodes || []).map((node) => {
            if (typeof node === 'string') {
                return node;
            }
            if (typeof node.children === 'string') {
                return node.children;
            }
            if (Array.isArray(node.children)) {
                return flattenText(node.children);
            }
            return '';
        }).join('');
    }

    function stripHtml(value) {
        const container = document.createElement('div');
        container.innerHTML = value || '';
        return container.textContent || container.innerText || '';
    }

    function baseWrapper(tag, className) {
        return {
            inheritAttrs: false,
            render() {
                const attrs = { ...this.$attrs };
                return Vue.h(tag, { ...attrs, class: [className, attrs.class] }, this.$slots.default ? this.$slots.default() : []);
            }
        };
    }

    const VApp = baseWrapper('div', 'wp-app');
    const VMain = baseWrapper('main', 'wp-main');
    const VToolbarTitle = baseWrapper('div', 'wp-toolbar-title');
    const VList = baseWrapper('div', 'wp-list');
    const VListItem = baseWrapper('div', 'wp-list-item');
    const VListItemGroup = baseWrapper('div', 'wp-list-item-group');
    const VListItemTitle = baseWrapper('div', 'wp-list-item-title');
    const VListItemSubtitle = baseWrapper('div', 'wp-list-item-subtitle');
    const VListItemContent = baseWrapper('div', 'wp-list-item-content');
    const VListItemIcon = baseWrapper('div', 'wp-list-item-icon');
    const VCardTitle = baseWrapper('div', 'wp-card-title');
    const VCardText = baseWrapper('div', 'wp-card-text');
    const VCardActions = baseWrapper('div', 'wp-card-actions');
    const VSheet = {
        inheritAttrs: false,
        props: ['rounded', 'color', 'flat'],
        render() {
            const attrs = { ...this.$attrs };
            const style = attrs.style ? [attrs.style] : [];
            if (this.color && this.color !== 'transparent') {
                style.push({ background: getColor(this.color) });
            }
            return Vue.h('div', { ...attrs, class: ['wp-sheet', this.flat && 'is-flat', attrs.class], style }, this.$slots.default ? this.$slots.default() : []);
        }
    };
    const VSpacer = { render() { return Vue.h('div', { class: 'wp-spacer' }); } };

    const VAppBar = {
        inheritAttrs: false,
        props: ['color', 'dark', 'app', 'dense'],
        render() {
            const attrs = { ...this.$attrs };
            const style = attrs.style ? [attrs.style] : [];
            if (this.color) {
                style.push({ background: getColor(this.color), color: '#fff' });
            }
            return Vue.h('header', { ...attrs, class: ['wp-app-bar', this.app && 'is-app', this.dense && 'is-dense', attrs.class], style }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VContainer = {
        inheritAttrs: false,
        props: ['fluid'],
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('div', { ...attrs, class: [this.fluid ? 'wp-container-fluid' : 'wp-container', attrs.class] }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VRow = {
        inheritAttrs: false,
        props: ['align', 'justify', 'dense'],
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('div', {
                ...attrs,
                class: [
                    'wp-row',
                    this.dense && 'wp-row-dense',
                    this.align && `wp-align-${this.align}`,
                    this.justify && `wp-justify-${this.justify}`,
                    attrs.class
                ]
            }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VCol = {
        inheritAttrs: false,
        props: ['cols', 'sm', 'md', 'lg'],
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('div', {
                ...attrs,
                class: [
                    'wp-col',
                    this.cols && `wp-col-${this.cols}`,
                    this.sm && `wp-sm-${this.sm}`,
                    this.md && `wp-md-${this.md}`,
                    this.lg && `wp-lg-${this.lg}`,
                    attrs.class
                ]
            }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VCard = {
        inheritAttrs: false,
        props: ['flat', 'outlined', 'elevation'],
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('section', { ...attrs, class: ['wp-card', this.flat && 'is-flat', this.outlined && 'is-outlined', attrs.class] }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VAvatar = {
        inheritAttrs: false,
        props: ['size'],
        render() {
            const attrs = { ...this.$attrs };
            const size = this.size ? `${this.size}px` : '64px';
            return Vue.h('div', { ...attrs, class: ['wp-avatar', attrs.class], style: [{ width: size, height: size }, attrs.style] }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VIcon = {
        inheritAttrs: false,
        props: ['right', 'color'],
        render() {
            const attrs = { ...this.$attrs };
            const rawName = getSlotText(this.$slots).replace(/^mdi-/, '');
            return Vue.h('i', {
                ...attrs,
                class: ['mdi', `mdi-${rawName || 'help-circle-outline'}`, 'wp-icon', this.right && 'is-right', attrs.class],
                style: [{ color: this.color ? getColor(this.color) : undefined }, attrs.style]
            });
        }
    };

    const VBtn = {
        inheritAttrs: false,
        props: ['text', 'block', 'outlined', 'color', 'large', 'href', 'type', 'icon', 'dark', 'loading', 'disabled'],
        render() {
            const attrs = { ...this.$attrs };
            const tag = this.href ? 'a' : 'button';
            const style = attrs.style ? [attrs.style] : [];
            const tone = this.color ? getColor(this.color) : null;
            if (tone) {
                if (this.text || this.outlined) {
                    style.push({ color: tone, borderColor: tone });
                } else {
                    style.push({ background: tone, borderColor: tone, color: '#fff' });
                }
            }
            return Vue.h(tag, {
                ...attrs,
                href: this.href,
                type: tag === 'button' ? (this.type || 'button') : undefined,
                disabled: tag === 'button' ? (this.disabled || this.loading) : undefined,
                class: ['wp-btn', this.text && 'is-text', this.block && 'is-block', this.outlined && 'is-outlined', this.large && 'is-large', this.icon && 'is-icon', attrs.class],
                style
            }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VBtnGroup = baseWrapper('div', 'wp-btn-group');

    const VForm = {
        inheritAttrs: false,
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('form', { ...attrs, class: attrs.class }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VTextField = {
        inheritAttrs: false,
        emits: ['update:modelValue'],
        props: {
            modelValue: [String, Number],
            modelModifiers: { type: Object, default: () => ({}) },
            label: String,
            type: { type: String, default: 'text' },
            prependIcon: String,
            prependInnerIcon: String,
            outlined: Boolean,
            autocomplete: String,
            required: Boolean,
            placeholder: String
        },
        methods: {
            updateValue(event) {
                let value = event.target.value;
                if (this.modelModifiers.trim) {
                    value = value.trim();
                }
                this.$emit('update:modelValue', value);
            }
        },
        render() {
            const attrs = { ...this.$attrs };
            const icon = this.prependInnerIcon || this.prependIcon;
            return Vue.h('label', { class: ['wp-field', attrs.class] }, [
                this.label ? Vue.h('span', { class: 'wp-field-label' }, this.label) : null,
                Vue.h('span', { class: ['wp-field-input-wrap', this.outlined && 'is-outlined'] }, [
                    icon ? Vue.h(VIcon, null, { default: () => [icon] }) : null,
                    Vue.h('input', {
                        ...attrs,
                        class: 'wp-field-input',
                        value: this.modelValue ?? '',
                        type: this.type,
                        autocomplete: this.autocomplete,
                        required: this.required,
                        placeholder: this.placeholder,
                        onInput: this.updateValue
                    })
                ])
            ]);
        }
    };

    const VSelect = {
        inheritAttrs: false,
        emits: ['update:modelValue'],
        props: {
            modelValue: [String, Number],
            items: { type: Array, default: () => [] },
            itemText: { type: String, default: 'text' },
            itemValue: { type: String, default: 'value' },
            label: String
        },
        methods: {
            normalizeItem(item) {
                if (item && typeof item === 'object') {
                    return {
                        text: stripHtml(item[this.itemText] ?? item.text ?? item.value ?? ''),
                        value: item[this.itemValue] ?? item.value ?? item.text ?? ''
                    };
                }
                return { text: item, value: item };
            }
        },
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('label', { class: ['wp-field', attrs.class] }, [
                this.label ? Vue.h('span', { class: 'wp-field-label' }, this.label) : null,
                Vue.h('span', { class: 'wp-field-input-wrap is-outlined' }, [
                    Vue.h('select', {
                        ...attrs,
                        class: 'wp-select',
                        value: this.modelValue ?? '',
                        onChange: (event) => this.$emit('update:modelValue', event.target.value)
                    }, this.items.map((item) => {
                        const normalized = this.normalizeItem(item);
                        return Vue.h('option', { value: normalized.value }, normalized.text);
                    }))
                ])
            ]);
        }
    };

    const VAlert = {
        inheritAttrs: false,
        props: ['type', 'outlined', 'dense'],
        render() {
            const attrs = { ...this.$attrs };
            return Vue.h('div', { ...attrs, class: ['wp-alert', this.type && `is-${this.type}`, attrs.class] }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VDialog = {
        inheritAttrs: false,
        emits: ['update:modelValue'],
        props: {
            modelValue: Boolean,
            persistent: Boolean,
            maxWidth: String
        },
        methods: {
            close() {
                if (!this.persistent) {
                    this.$emit('update:modelValue', false);
                }
            }
        },
        render() {
            if (!this.modelValue) {
                return null;
            }
            const attrs = { ...this.$attrs };
            return Vue.h('div', { class: 'wp-dialog-overlay', onClick: this.close }, [
                Vue.h('div', {
                    ...attrs,
                    class: ['wp-dialog-card', attrs.class],
                    style: [{ maxWidth: this.maxWidth || '600px' }, attrs.style],
                    onClick: (event) => event.stopPropagation()
                }, this.$slots.default ? this.$slots.default() : [])
            ]);
        }
    };

    const VMenu = {
        inheritAttrs: false,
        data() {
            return { open: false };
        },
        mounted() {
            this.handleOutsideClick = (event) => {
                if (this.$el && !this.$el.contains(event.target)) {
                    this.open = false;
                }
            };
            document.addEventListener('click', this.handleOutsideClick);
        },
        beforeUnmount() {
            document.removeEventListener('click', this.handleOutsideClick);
        },
        methods: {
            toggle(event) {
                event.preventDefault();
                this.open = !this.open;
            },
            closeOnContentClick(event) {
                if (event.target.closest('a,button')) {
                    this.open = false;
                }
            }
        },
        render() {
            const attrs = { ...this.$attrs };
            const activator = this.$slots.activator ? this.$slots.activator({
                on: { click: this.toggle },
                attrs: { 'aria-haspopup': 'menu', 'aria-expanded': String(this.open) }
            }) : null;

            return Vue.h('div', { ...attrs, class: ['wp-menu', attrs.class] }, [
                activator,
                this.open ? Vue.h('div', { class: 'wp-menu-content', onClick: this.closeOnContentClick }, this.$slots.default ? this.$slots.default() : []) : null
            ]);
        }
    };

    const VFooter = {
        inheritAttrs: false,
        props: ['fixed', 'padless', 'color'],
        render() {
            const attrs = { ...this.$attrs };
            const style = attrs.style ? [attrs.style] : [];
            if (this.color && this.color !== 'transparent') {
                style.push({ background: getColor(this.color) });
            }
            return Vue.h('footer', { ...attrs, class: ['wp-footer', this.fixed && 'is-fixed', attrs.class], style }, this.$slots.default ? this.$slots.default() : []);
        }
    };

    const VDataTable = {
        inheritAttrs: false,
        props: {
            headers: { type: Array, default: () => [] },
            items: { type: Array, default: () => [] },
            itemsPerPage: Number
        },
        methods: {
            normalizeHeaders() {
                if (this.headers && this.headers.length) {
                    return this.headers.map((header) => ({
                        text: header.text || header.title || header.value,
                        value: header.value || header.key || header.text
                    }));
                }
                const firstItem = this.items[0] || {};
                return Object.keys(firstItem).map((key) => ({ text: key, value: key }));
            }
        },
        render() {
            const attrs = { ...this.$attrs };
            const headers = this.normalizeHeaders();
            const items = this.itemsPerPage ? this.items.slice(0, this.itemsPerPage) : this.items;

            return Vue.h('div', { ...attrs, class: ['wp-data-table-wrap', attrs.class] }, [
                Vue.h('table', { class: 'wp-data-table' }, [
                    Vue.h('thead', null, [
                        Vue.h('tr', null, headers.map((header) => Vue.h('th', { key: header.value }, header.text)))
                    ]),
                    Vue.h('tbody', null, items.length ? items.map((item, rowIndex) => Vue.h('tr', { key: item.id || rowIndex }, headers.map((header) => {
                        const slot = this.$slots[`item.${header.value}`];
                        return Vue.h('td', { key: header.value }, slot ? slot({ item }) : String(item[header.value] ?? ''));
                    }))) : [
                        Vue.h('tr', null, [
                            Vue.h('td', { colspan: headers.length || 1, class: 'wp-data-table-empty' }, 'No data available.')
                        ])
                    ])
                ])
            ]);
        }
    };

    const VSubheader = baseWrapper('div', 'wp-subheader');

    function install(app) {
        injectStyles();
        ensureMdi();

        app.component('v-app', VApp);
        app.component('v-main', VMain);
        app.component('v-app-bar', VAppBar);
        app.component('v-toolbar-title', VToolbarTitle);
        app.component('v-spacer', VSpacer);
        app.component('v-container', VContainer);
        app.component('v-row', VRow);
        app.component('v-col', VCol);
        app.component('v-btn', VBtn);
        app.component('v-btn-group', VBtnGroup);
        app.component('v-menu', VMenu);
        app.component('v-list', VList);
        app.component('v-list-item', VListItem);
        app.component('v-list-item-group', VListItemGroup);
        app.component('v-list-item-title', VListItemTitle);
        app.component('v-list-item-subtitle', VListItemSubtitle);
        app.component('v-list-item-content', VListItemContent);
        app.component('v-list-item-icon', VListItemIcon);
        app.component('v-icon', VIcon);
        app.component('v-avatar', VAvatar);
        app.component('v-card', VCard);
        app.component('v-card-title', VCardTitle);
        app.component('v-card-text', VCardText);
        app.component('v-card-actions', VCardActions);
        app.component('v-sheet', VSheet);
        app.component('v-form', VForm);
        app.component('v-text-field', VTextField);
        app.component('v-select', VSelect);
        app.component('v-alert', VAlert);
        app.component('v-dialog', VDialog);
        app.component('v-data-table', VDataTable);
        app.component('v-footer', VFooter);
        app.component('v-subheader', VSubheader);
        return app;
    }

    function mountApp(options) {
        const selector = options.el || '#app';
        const normalized = { ...options };
        delete normalized.el;
        delete normalized.vuetify;

        if (normalized.data && typeof normalized.data !== 'function') {
            const source = normalized.data;
            normalized.data = function () {
                return source;
            };
        }

        const app = Vue.createApp(normalized);
        install(app);
        return app.mount(selector);
    }

    window.WebPortalVue3 = { install, mountApp };
})();

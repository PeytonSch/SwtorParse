
(function(l, r) { if (l.getElementById('livereloadscript')) return; r = l.createElement('script'); r.async = 1; r.src = '//' + (window.location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1'; r.id = 'livereloadscript'; l.getElementsByTagName('head')[0].appendChild(r) })(window.document);
var app = (function () {
    'use strict';

    function noop() { }
    function add_location(element, file, line, column, char) {
        element.__svelte_meta = {
            loc: { file, line, column, char }
        };
    }
    function run(fn) {
        return fn();
    }
    function blank_object() {
        return Object.create(null);
    }
    function run_all(fns) {
        fns.forEach(run);
    }
    function is_function(thing) {
        return typeof thing === 'function';
    }
    function safe_not_equal(a, b) {
        return a != a ? b == b : a !== b || ((a && typeof a === 'object') || typeof a === 'function');
    }
    function is_empty(obj) {
        return Object.keys(obj).length === 0;
    }

    function append(target, node) {
        target.appendChild(node);
    }
    function insert(target, node, anchor) {
        target.insertBefore(node, anchor || null);
    }
    function detach(node) {
        node.parentNode.removeChild(node);
    }
    function element(name) {
        return document.createElement(name);
    }
    function svg_element(name) {
        return document.createElementNS('http://www.w3.org/2000/svg', name);
    }
    function text(data) {
        return document.createTextNode(data);
    }
    function space() {
        return text(' ');
    }
    function listen(node, event, handler, options) {
        node.addEventListener(event, handler, options);
        return () => node.removeEventListener(event, handler, options);
    }
    function attr(node, attribute, value) {
        if (value == null)
            node.removeAttribute(attribute);
        else if (node.getAttribute(attribute) !== value)
            node.setAttribute(attribute, value);
    }
    function children(element) {
        return Array.from(element.childNodes);
    }
    function set_style(node, key, value, important) {
        node.style.setProperty(key, value, important ? 'important' : '');
    }
    function custom_event(type, detail) {
        const e = document.createEvent('CustomEvent');
        e.initCustomEvent(type, false, false, detail);
        return e;
    }

    let current_component;
    function set_current_component(component) {
        current_component = component;
    }

    const dirty_components = [];
    const binding_callbacks = [];
    const render_callbacks = [];
    const flush_callbacks = [];
    const resolved_promise = Promise.resolve();
    let update_scheduled = false;
    function schedule_update() {
        if (!update_scheduled) {
            update_scheduled = true;
            resolved_promise.then(flush);
        }
    }
    function add_render_callback(fn) {
        render_callbacks.push(fn);
    }
    let flushing = false;
    const seen_callbacks = new Set();
    function flush() {
        if (flushing)
            return;
        flushing = true;
        do {
            // first, call beforeUpdate functions
            // and update components
            for (let i = 0; i < dirty_components.length; i += 1) {
                const component = dirty_components[i];
                set_current_component(component);
                update(component.$$);
            }
            set_current_component(null);
            dirty_components.length = 0;
            while (binding_callbacks.length)
                binding_callbacks.pop()();
            // then, once components are updated, call
            // afterUpdate functions. This may cause
            // subsequent updates...
            for (let i = 0; i < render_callbacks.length; i += 1) {
                const callback = render_callbacks[i];
                if (!seen_callbacks.has(callback)) {
                    // ...so guard against infinite loops
                    seen_callbacks.add(callback);
                    callback();
                }
            }
            render_callbacks.length = 0;
        } while (dirty_components.length);
        while (flush_callbacks.length) {
            flush_callbacks.pop()();
        }
        update_scheduled = false;
        flushing = false;
        seen_callbacks.clear();
    }
    function update($$) {
        if ($$.fragment !== null) {
            $$.update();
            run_all($$.before_update);
            const dirty = $$.dirty;
            $$.dirty = [-1];
            $$.fragment && $$.fragment.p($$.ctx, dirty);
            $$.after_update.forEach(add_render_callback);
        }
    }
    const outroing = new Set();
    function transition_in(block, local) {
        if (block && block.i) {
            outroing.delete(block);
            block.i(local);
        }
    }
    function mount_component(component, target, anchor, customElement) {
        const { fragment, on_mount, on_destroy, after_update } = component.$$;
        fragment && fragment.m(target, anchor);
        if (!customElement) {
            // onMount happens before the initial afterUpdate
            add_render_callback(() => {
                const new_on_destroy = on_mount.map(run).filter(is_function);
                if (on_destroy) {
                    on_destroy.push(...new_on_destroy);
                }
                else {
                    // Edge case - component was destroyed immediately,
                    // most likely as a result of a binding initialising
                    run_all(new_on_destroy);
                }
                component.$$.on_mount = [];
            });
        }
        after_update.forEach(add_render_callback);
    }
    function destroy_component(component, detaching) {
        const $$ = component.$$;
        if ($$.fragment !== null) {
            run_all($$.on_destroy);
            $$.fragment && $$.fragment.d(detaching);
            // TODO null out other refs, including component.$$ (but need to
            // preserve final state?)
            $$.on_destroy = $$.fragment = null;
            $$.ctx = [];
        }
    }
    function make_dirty(component, i) {
        if (component.$$.dirty[0] === -1) {
            dirty_components.push(component);
            schedule_update();
            component.$$.dirty.fill(0);
        }
        component.$$.dirty[(i / 31) | 0] |= (1 << (i % 31));
    }
    function init(component, options, instance, create_fragment, not_equal, props, dirty = [-1]) {
        const parent_component = current_component;
        set_current_component(component);
        const $$ = component.$$ = {
            fragment: null,
            ctx: null,
            // state
            props,
            update: noop,
            not_equal,
            bound: blank_object(),
            // lifecycle
            on_mount: [],
            on_destroy: [],
            on_disconnect: [],
            before_update: [],
            after_update: [],
            context: new Map(parent_component ? parent_component.$$.context : options.context || []),
            // everything else
            callbacks: blank_object(),
            dirty,
            skip_bound: false
        };
        let ready = false;
        $$.ctx = instance
            ? instance(component, options.props || {}, (i, ret, ...rest) => {
                const value = rest.length ? rest[0] : ret;
                if ($$.ctx && not_equal($$.ctx[i], $$.ctx[i] = value)) {
                    if (!$$.skip_bound && $$.bound[i])
                        $$.bound[i](value);
                    if (ready)
                        make_dirty(component, i);
                }
                return ret;
            })
            : [];
        $$.update();
        ready = true;
        run_all($$.before_update);
        // `false` as a special case of no DOM component
        $$.fragment = create_fragment ? create_fragment($$.ctx) : false;
        if (options.target) {
            if (options.hydrate) {
                const nodes = children(options.target);
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                $$.fragment && $$.fragment.l(nodes);
                nodes.forEach(detach);
            }
            else {
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                $$.fragment && $$.fragment.c();
            }
            if (options.intro)
                transition_in(component.$$.fragment);
            mount_component(component, options.target, options.anchor, options.customElement);
            flush();
        }
        set_current_component(parent_component);
    }
    /**
     * Base class for Svelte components. Used when dev=false.
     */
    class SvelteComponent {
        $destroy() {
            destroy_component(this, 1);
            this.$destroy = noop;
        }
        $on(type, callback) {
            const callbacks = (this.$$.callbacks[type] || (this.$$.callbacks[type] = []));
            callbacks.push(callback);
            return () => {
                const index = callbacks.indexOf(callback);
                if (index !== -1)
                    callbacks.splice(index, 1);
            };
        }
        $set($$props) {
            if (this.$$set && !is_empty($$props)) {
                this.$$.skip_bound = true;
                this.$$set($$props);
                this.$$.skip_bound = false;
            }
        }
    }

    function dispatch_dev(type, detail) {
        document.dispatchEvent(custom_event(type, Object.assign({ version: '3.38.2' }, detail)));
    }
    function append_dev(target, node) {
        dispatch_dev('SvelteDOMInsert', { target, node });
        append(target, node);
    }
    function insert_dev(target, node, anchor) {
        dispatch_dev('SvelteDOMInsert', { target, node, anchor });
        insert(target, node, anchor);
    }
    function detach_dev(node) {
        dispatch_dev('SvelteDOMRemove', { node });
        detach(node);
    }
    function listen_dev(node, event, handler, options, has_prevent_default, has_stop_propagation) {
        const modifiers = options === true ? ['capture'] : options ? Array.from(Object.keys(options)) : [];
        if (has_prevent_default)
            modifiers.push('preventDefault');
        if (has_stop_propagation)
            modifiers.push('stopPropagation');
        dispatch_dev('SvelteDOMAddEventListener', { node, event, handler, modifiers });
        const dispose = listen(node, event, handler, options);
        return () => {
            dispatch_dev('SvelteDOMRemoveEventListener', { node, event, handler, modifiers });
            dispose();
        };
    }
    function attr_dev(node, attribute, value) {
        attr(node, attribute, value);
        if (value == null)
            dispatch_dev('SvelteDOMRemoveAttribute', { node, attribute });
        else
            dispatch_dev('SvelteDOMSetAttribute', { node, attribute, value });
    }
    function set_data_dev(text, data) {
        data = '' + data;
        if (text.wholeText === data)
            return;
        dispatch_dev('SvelteDOMSetData', { node: text, data });
        text.data = data;
    }
    function validate_slots(name, slot, keys) {
        for (const slot_key of Object.keys(slot)) {
            if (!~keys.indexOf(slot_key)) {
                console.warn(`<${name}> received an unexpected slot "${slot_key}".`);
            }
        }
    }
    /**
     * Base class for Svelte components with some minor dev-enhancements. Used when dev=true.
     */
    class SvelteComponentDev extends SvelteComponent {
        constructor(options) {
            if (!options || (!options.target && !options.$$inline)) {
                throw new Error("'target' is a required option");
            }
            super();
        }
        $destroy() {
            super.$destroy();
            this.$destroy = () => {
                console.warn('Component was already destroyed'); // eslint-disable-line no-console
            };
        }
        $capture_state() { }
        $inject_state() { }
    }

    /* src\personal_overlay\App.svelte generated by Svelte v3.38.2 */

    const file = "src\\personal_overlay\\App.svelte";

    function create_fragment(ctx) {
    	let div2;
    	let div0;
    	let t1;
    	let div1;
    	let button0;
    	let svg0;
    	let path0;
    	let t2;
    	let button1;
    	let svg1;
    	let path1;
    	let t3;
    	let ul;
    	let li0;
    	let span0;
    	let t5;
    	let span1;

    	let t6_value = /*personalStats*/ ctx[0].apm.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t6;
    	let t7;
    	let li1;
    	let span2;
    	let t9;
    	let span3;

    	let t10_value = /*personalStats*/ ctx[0].gcd.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t10;
    	let t11;
    	let li2;
    	let span4;
    	let t13;
    	let span5;

    	let t14_value = /*personalStats*/ ctx[0].dps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t14;
    	let t15;
    	let li3;
    	let span6;
    	let t17;
    	let span7;

    	let t18_value = /*personalStats*/ ctx[0].hps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t18;
    	let t19;
    	let li4;
    	let span8;
    	let t21;
    	let span9;

    	let t22_value = /*personalStats*/ ctx[0].tps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t22;
    	let t23;
    	let li5;
    	let span10;
    	let t25;
    	let span11;

    	let t26_value = /*personalStats*/ ctx[0].dtps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t26;
    	let t27;
    	let li6;
    	let span12;
    	let t29;
    	let span13;

    	let t30_value = /*personalStats*/ ctx[0].htps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t30;
    	let t31;
    	let li7;
    	let span14;
    	let t33;
    	let span15;

    	let t34_value = /*personalStats*/ ctx[0].dabps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t34;
    	let t35;
    	let li8;
    	let span16;
    	let t37;
    	let span17;

    	let t38_value = /*personalStats*/ ctx[0].delay.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t38;
    	let t39;
    	let mounted;
    	let dispose;

    	const block = {
    		c: function create() {
    			div2 = element("div");
    			div0 = element("div");
    			div0.textContent = "Personal";
    			t1 = space();
    			div1 = element("div");
    			button0 = element("button");
    			svg0 = svg_element("svg");
    			path0 = svg_element("path");
    			t2 = space();
    			button1 = element("button");
    			svg1 = svg_element("svg");
    			path1 = svg_element("path");
    			t3 = space();
    			ul = element("ul");
    			li0 = element("li");
    			span0 = element("span");
    			span0.textContent = "APM:";
    			t5 = space();
    			span1 = element("span");
    			t6 = text(t6_value);
    			t7 = space();
    			li1 = element("li");
    			span2 = element("span");
    			span2.textContent = "GCD:";
    			t9 = space();
    			span3 = element("span");
    			t10 = text(t10_value);
    			t11 = space();
    			li2 = element("li");
    			span4 = element("span");
    			span4.textContent = "DPS:";
    			t13 = space();
    			span5 = element("span");
    			t14 = text(t14_value);
    			t15 = space();
    			li3 = element("li");
    			span6 = element("span");
    			span6.textContent = "HPS:";
    			t17 = space();
    			span7 = element("span");
    			t18 = text(t18_value);
    			t19 = space();
    			li4 = element("li");
    			span8 = element("span");
    			span8.textContent = "TPS:";
    			t21 = space();
    			span9 = element("span");
    			t22 = text(t22_value);
    			t23 = space();
    			li5 = element("li");
    			span10 = element("span");
    			span10.textContent = "DTPS:";
    			t25 = space();
    			span11 = element("span");
    			t26 = text(t26_value);
    			t27 = space();
    			li6 = element("li");
    			span12 = element("span");
    			span12.textContent = "HTPS:";
    			t29 = space();
    			span13 = element("span");
    			t30 = text(t30_value);
    			t31 = space();
    			li7 = element("li");
    			span14 = element("span");
    			span14.textContent = "DAPS:";
    			t33 = space();
    			span15 = element("span");
    			t34 = text(t34_value);
    			t35 = space();
    			li8 = element("li");
    			span16 = element("span");
    			span16.textContent = "Delay:";
    			t37 = space();
    			span17 = element("span");
    			t38 = text(t38_value);
    			t39 = text("s ago");
    			attr_dev(div0, "class", "f6");
    			add_location(div0, file, 25, 1, 466);
    			attr_dev(path0, "stroke-linecap", "round");
    			attr_dev(path0, "stroke-linejoin", "round");
    			attr_dev(path0, "stroke-width", "2");
    			attr_dev(path0, "d", "M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4");
    			add_location(path0, file, 34, 4, 806);
    			attr_dev(svg0, "xmlns", "http://www.w3.org/2000/svg");
    			attr_dev(svg0, "style", "width: 16px; height 16px; transform: rotate(41deg)");
    			attr_dev(svg0, "fill", "none");
    			attr_dev(svg0, "viewBox", "0 0 24 24");
    			attr_dev(svg0, "stroke", "currentColor");
    			add_location(svg0, file, 28, 3, 622);
    			attr_dev(button0, "class", "close-button overlay-title-bar-drag mr-2");
    			attr_dev(button0, "type", "button");
    			attr_dev(button0, "om:click", /*close*/ ctx[1]);
    			set_style(button0, "z-index", "10");
    			add_location(button0, file, 27, 2, 508);
    			attr_dev(path1, "fill-rule", "evenodd");
    			attr_dev(path1, "d", "M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z");
    			attr_dev(path1, "clip-rule", "evenodd");
    			add_location(path1, file, 43, 4, 1230);
    			attr_dev(svg1, "xmlns", "http://www.w3.org/2000/svg");
    			attr_dev(svg1, "style", "width: 16px; height 16px;");
    			attr_dev(svg1, "viewBox", "0 0 20 20");
    			attr_dev(svg1, "fill", "currentColor");
    			add_location(svg1, file, 42, 3, 1110);
    			attr_dev(button1, "class", "close-button");
    			attr_dev(button1, "type", "button");
    			set_style(button1, "z-index", "10");
    			add_location(button1, file, 41, 2, 1024);
    			add_location(div1, file, 26, 1, 499);
    			attr_dev(div2, "class", "p-1 overlay-title-bar d-flex flex-justify-between flex-items-center");
    			add_location(div2, file, 24, 0, 382);
    			attr_dev(span0, "class", "f6");
    			add_location(span0, file, 55, 2, 1647);
    			attr_dev(span1, "class", "text-bold");
    			add_location(span1, file, 56, 2, 1679);
    			attr_dev(li0, "class", "my-1 d-flex flex-justify-between");
    			add_location(li0, file, 54, 1, 1598);
    			attr_dev(span2, "class", "f6");
    			add_location(span2, file, 66, 2, 1912);
    			attr_dev(span3, "class", "text-bold");
    			set_style(span3, "color", "#b392f0");
    			add_location(span3, file, 67, 2, 1944);
    			attr_dev(li1, "class", "my-1 d-flex flex-justify-between");
    			add_location(li1, file, 65, 1, 1863);
    			attr_dev(span4, "class", "f6");
    			add_location(span4, file, 77, 2, 2200);
    			attr_dev(span5, "class", "text-bold");
    			set_style(span5, "color", "#f97583");
    			add_location(span5, file, 78, 2, 2232);
    			attr_dev(li2, "class", "my-1 d-flex flex-justify-between");
    			add_location(li2, file, 76, 1, 2151);
    			attr_dev(span6, "class", "f6");
    			add_location(span6, file, 88, 2, 2489);
    			attr_dev(span7, "class", "text-bold");
    			set_style(span7, "color", "#85e89d");
    			add_location(span7, file, 89, 2, 2521);
    			attr_dev(li3, "class", "my-1 d-flex flex-justify-between");
    			add_location(li3, file, 87, 1, 2440);
    			attr_dev(span8, "class", "f6");
    			add_location(span8, file, 99, 2, 2778);
    			attr_dev(span9, "class", "text-bold");
    			set_style(span9, "color", "#ffea7f");
    			add_location(span9, file, 100, 2, 2810);
    			attr_dev(li4, "class", "my-1 d-flex flex-justify-between");
    			add_location(li4, file, 98, 1, 2729);
    			attr_dev(span10, "class", "f6");
    			add_location(span10, file, 110, 2, 3073);
    			attr_dev(span11, "class", "text-bold");
    			set_style(span11, "color", "#ff7b72");
    			add_location(span11, file, 111, 2, 3106);
    			attr_dev(li5, "class", "my-1 d-flex flex-justify-between mt-2");
    			add_location(li5, file, 109, 1, 3019);
    			attr_dev(span12, "class", "f6");
    			add_location(span12, file, 121, 2, 3365);
    			attr_dev(span13, "class", "text-bold");
    			set_style(span13, "color", "#3fb950");
    			add_location(span13, file, 122, 2, 3398);
    			attr_dev(li6, "class", "my-1 d-flex flex-justify-between");
    			add_location(li6, file, 120, 1, 3316);
    			attr_dev(span14, "class", "f6");
    			add_location(span14, file, 132, 2, 3657);
    			attr_dev(span15, "class", "text-bold");
    			set_style(span15, "color", "#58a6ff");
    			add_location(span15, file, 133, 2, 3690);
    			attr_dev(li7, "class", "my-1 d-flex flex-justify-between");
    			add_location(li7, file, 131, 1, 3608);
    			attr_dev(span16, "class", "f6");
    			add_location(span16, file, 143, 2, 3956);
    			attr_dev(span17, "class", "text-bold");
    			add_location(span17, file, 144, 2, 3990);
    			attr_dev(li8, "class", "my-1 d-flex flex-justify-between mt-2");
    			add_location(li8, file, 142, 1, 3902);
    			attr_dev(ul, "class", "p-1 f6");
    			add_location(ul, file, 52, 0, 1561);
    		},
    		l: function claim(nodes) {
    			throw new Error("options.hydrate only works if the component was compiled with the `hydratable: true` option");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div2, anchor);
    			append_dev(div2, div0);
    			append_dev(div2, t1);
    			append_dev(div2, div1);
    			append_dev(div1, button0);
    			append_dev(button0, svg0);
    			append_dev(svg0, path0);
    			append_dev(div1, t2);
    			append_dev(div1, button1);
    			append_dev(button1, svg1);
    			append_dev(svg1, path1);
    			insert_dev(target, t3, anchor);
    			insert_dev(target, ul, anchor);
    			append_dev(ul, li0);
    			append_dev(li0, span0);
    			append_dev(li0, t5);
    			append_dev(li0, span1);
    			append_dev(span1, t6);
    			append_dev(ul, t7);
    			append_dev(ul, li1);
    			append_dev(li1, span2);
    			append_dev(li1, t9);
    			append_dev(li1, span3);
    			append_dev(span3, t10);
    			append_dev(ul, t11);
    			append_dev(ul, li2);
    			append_dev(li2, span4);
    			append_dev(li2, t13);
    			append_dev(li2, span5);
    			append_dev(span5, t14);
    			append_dev(ul, t15);
    			append_dev(ul, li3);
    			append_dev(li3, span6);
    			append_dev(li3, t17);
    			append_dev(li3, span7);
    			append_dev(span7, t18);
    			append_dev(ul, t19);
    			append_dev(ul, li4);
    			append_dev(li4, span8);
    			append_dev(li4, t21);
    			append_dev(li4, span9);
    			append_dev(span9, t22);
    			append_dev(ul, t23);
    			append_dev(ul, li5);
    			append_dev(li5, span10);
    			append_dev(li5, t25);
    			append_dev(li5, span11);
    			append_dev(span11, t26);
    			append_dev(ul, t27);
    			append_dev(ul, li6);
    			append_dev(li6, span12);
    			append_dev(li6, t29);
    			append_dev(li6, span13);
    			append_dev(span13, t30);
    			append_dev(ul, t31);
    			append_dev(ul, li7);
    			append_dev(li7, span14);
    			append_dev(li7, t33);
    			append_dev(li7, span15);
    			append_dev(span15, t34);
    			append_dev(ul, t35);
    			append_dev(ul, li8);
    			append_dev(li8, span16);
    			append_dev(li8, t37);
    			append_dev(li8, span17);
    			append_dev(span17, t38);
    			append_dev(span17, t39);

    			if (!mounted) {
    				dispose = listen_dev(button1, "click", /*close*/ ctx[1], false, false, false);
    				mounted = true;
    			}
    		},
    		p: function update(ctx, [dirty]) {
    			if (dirty & /*personalStats*/ 1 && t6_value !== (t6_value = /*personalStats*/ ctx[0].apm.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t6, t6_value);

    			if (dirty & /*personalStats*/ 1 && t10_value !== (t10_value = /*personalStats*/ ctx[0].gcd.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t10, t10_value);

    			if (dirty & /*personalStats*/ 1 && t14_value !== (t14_value = /*personalStats*/ ctx[0].dps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t14, t14_value);

    			if (dirty & /*personalStats*/ 1 && t18_value !== (t18_value = /*personalStats*/ ctx[0].hps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t18, t18_value);

    			if (dirty & /*personalStats*/ 1 && t22_value !== (t22_value = /*personalStats*/ ctx[0].tps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t22, t22_value);

    			if (dirty & /*personalStats*/ 1 && t26_value !== (t26_value = /*personalStats*/ ctx[0].dtps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t26, t26_value);

    			if (dirty & /*personalStats*/ 1 && t30_value !== (t30_value = /*personalStats*/ ctx[0].htps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t30, t30_value);

    			if (dirty & /*personalStats*/ 1 && t34_value !== (t34_value = /*personalStats*/ ctx[0].dabps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t34, t34_value);

    			if (dirty & /*personalStats*/ 1 && t38_value !== (t38_value = /*personalStats*/ ctx[0].delay.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t38, t38_value);
    		},
    		i: noop,
    		o: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div2);
    			if (detaching) detach_dev(t3);
    			if (detaching) detach_dev(ul);
    			mounted = false;
    			dispose();
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_fragment.name,
    		type: "component",
    		source: "",
    		ctx
    	});

    	return block;
    }

    function instance($$self, $$props, $$invalidate) {
    	let { $$slots: slots = {}, $$scope } = $$props;
    	validate_slots("App", slots, []);
    	const { ipcRenderer } = require("electron");

    	let personalStats = {
    		apm: 0,
    		gcd: 1.5,
    		dps: 0,
    		hps: 0,
    		dtps: 0,
    		htps: 0,
    		dabps: 0,
    		tps: 0,
    		delay: 0
    	};

    	ipcRenderer.on("update_personal_stats", function (event, arg) {
    		$$invalidate(0, personalStats = arg);
    	});

    	function close() {
    		ipcRenderer.invoke("close_personal_overlay");
    	}

    	const writable_props = [];

    	Object.keys($$props).forEach(key => {
    		if (!~writable_props.indexOf(key) && key.slice(0, 2) !== "$$") console.warn(`<App> was created with unknown prop '${key}'`);
    	});

    	$$self.$capture_state = () => ({ ipcRenderer, personalStats, close });

    	$$self.$inject_state = $$props => {
    		if ("personalStats" in $$props) $$invalidate(0, personalStats = $$props.personalStats);
    	};

    	if ($$props && "$$inject" in $$props) {
    		$$self.$inject_state($$props.$$inject);
    	}

    	return [personalStats, close];
    }

    class App extends SvelteComponentDev {
    	constructor(options) {
    		super(options);
    		init(this, options, instance, create_fragment, safe_not_equal, {});

    		dispatch_dev("SvelteRegisterComponent", {
    			component: this,
    			tagName: "App",
    			options,
    			id: create_fragment.name
    		});
    	}
    }

    const app = new App({
    	target: document.body,
    });

    return app;

}());
//# sourceMappingURL=personal_overlay.js.map

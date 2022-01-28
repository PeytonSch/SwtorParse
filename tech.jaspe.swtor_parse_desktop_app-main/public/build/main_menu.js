
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
    function destroy_each(iterations, detaching) {
        for (let i = 0; i < iterations.length; i += 1) {
            if (iterations[i])
                iterations[i].d(detaching);
        }
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
    function empty() {
        return text('');
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
    function select_option(select, value) {
        for (let i = 0; i < select.options.length; i += 1) {
            const option = select.options[i];
            if (option.__value === value) {
                option.selected = true;
                return;
            }
        }
    }
    function select_value(select) {
        const selected_option = select.querySelector(':checked') || select.options[0];
        return selected_option && selected_option.__value;
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
    function tick() {
        schedule_update();
        return resolved_promise;
    }
    function add_render_callback(fn) {
        render_callbacks.push(fn);
    }
    function add_flush_callback(fn) {
        flush_callbacks.push(fn);
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
    let outros;
    function group_outros() {
        outros = {
            r: 0,
            c: [],
            p: outros // parent group
        };
    }
    function check_outros() {
        if (!outros.r) {
            run_all(outros.c);
        }
        outros = outros.p;
    }
    function transition_in(block, local) {
        if (block && block.i) {
            outroing.delete(block);
            block.i(local);
        }
    }
    function transition_out(block, local, detach, callback) {
        if (block && block.o) {
            if (outroing.has(block))
                return;
            outroing.add(block);
            outros.c.push(() => {
                outroing.delete(block);
                if (callback) {
                    if (detach)
                        block.d(1);
                    callback();
                }
            });
            block.o(local);
        }
    }

    const globals = (typeof window !== 'undefined'
        ? window
        : typeof globalThis !== 'undefined'
            ? globalThis
            : global);

    function bind(component, name, callback) {
        const index = component.$$.props[name];
        if (index !== undefined) {
            component.$$.bound[index] = callback;
            callback(component.$$.ctx[index]);
        }
    }
    function create_component(block) {
        block && block.c();
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
    function prop_dev(node, property, value) {
        node[property] = value;
        dispatch_dev('SvelteDOMSetProperty', { node, property, value });
    }
    function set_data_dev(text, data) {
        data = '' + data;
        if (text.wholeText === data)
            return;
        dispatch_dev('SvelteDOMSetData', { node: text, data });
        text.data = data;
    }
    function validate_each_argument(arg) {
        if (typeof arg !== 'string' && !(arg && typeof arg === 'object' && 'length' in arg)) {
            let msg = '{#each} only iterates over array-like objects.';
            if (typeof Symbol === 'function' && arg && Symbol.iterator in arg) {
                msg += ' You can use a spread to convert this iterable into an array.';
            }
            throw new Error(msg);
        }
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

    /* src\components\Loading.svelte generated by Svelte v3.38.2 */

    const file = "src\\components\\Loading.svelte";

    function create_fragment(ctx) {
    	let div2;
    	let div1;
    	let div0;
    	let p;
    	let span0;
    	let t0;
    	let t1_value = /*value*/ ctx[0].toFixed(2) + "";
    	let t1;
    	let t2;
    	let span1;
    	let t3;
    	let span3;
    	let span2;

    	const block = {
    		c: function create() {
    			div2 = element("div");
    			div1 = element("div");
    			div0 = element("div");
    			p = element("p");
    			span0 = element("span");
    			t0 = text("Loading ");
    			t1 = text(t1_value);
    			t2 = text("%");
    			span1 = element("span");
    			t3 = space();
    			span3 = element("span");
    			span2 = element("span");
    			add_location(span0, file, 8, 4, 311);
    			attr_dev(span1, "class", "AnimatedEllipsis");
    			add_location(span1, file, 8, 44, 351);
    			add_location(p, file, 7, 3, 303);
    			attr_dev(span2, "class", "Progress-item color-bg-success-inverse");
    			set_style(span2, "width", /*value*/ ctx[0] + "%");
    			add_location(span2, file, 12, 4, 446);
    			attr_dev(span3, "class", "mt-2 Progress Progress--large");
    			add_location(span3, file, 11, 3, 397);
    			attr_dev(div0, "class", "d-flex flex-column p-4 color-shadow-medium rounded");
    			set_style(div0, "width", "300px");
    			set_style(div0, "background", "#0d1117");
    			add_location(div0, file, 6, 2, 192);
    			attr_dev(div1, "class", "flex-self-center d-flex flex-items-center flex-column");
    			set_style(div1, "opacity", "100%");
    			add_location(div1, file, 5, 1, 99);
    			attr_dev(div2, "class", "delay d-flex flex-justify-center overlay");
    			add_location(div2, file, 4, 0, 43);
    		},
    		l: function claim(nodes) {
    			throw new Error("options.hydrate only works if the component was compiled with the `hydratable: true` option");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div2, anchor);
    			append_dev(div2, div1);
    			append_dev(div1, div0);
    			append_dev(div0, p);
    			append_dev(p, span0);
    			append_dev(span0, t0);
    			append_dev(span0, t1);
    			append_dev(span0, t2);
    			append_dev(p, span1);
    			append_dev(div0, t3);
    			append_dev(div0, span3);
    			append_dev(span3, span2);
    		},
    		p: function update(ctx, [dirty]) {
    			if (dirty & /*value*/ 1 && t1_value !== (t1_value = /*value*/ ctx[0].toFixed(2) + "")) set_data_dev(t1, t1_value);

    			if (dirty & /*value*/ 1) {
    				set_style(span2, "width", /*value*/ ctx[0] + "%");
    			}
    		},
    		i: noop,
    		o: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div2);
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
    	validate_slots("Loading", slots, []);
    	let { value = 0 } = $$props;
    	const writable_props = ["value"];

    	Object.keys($$props).forEach(key => {
    		if (!~writable_props.indexOf(key) && key.slice(0, 2) !== "$$") console.warn(`<Loading> was created with unknown prop '${key}'`);
    	});

    	$$self.$$set = $$props => {
    		if ("value" in $$props) $$invalidate(0, value = $$props.value);
    	};

    	$$self.$capture_state = () => ({ value });

    	$$self.$inject_state = $$props => {
    		if ("value" in $$props) $$invalidate(0, value = $$props.value);
    	};

    	if ($$props && "$$inject" in $$props) {
    		$$self.$inject_state($$props.$$inject);
    	}

    	return [value];
    }

    class Loading extends SvelteComponentDev {
    	constructor(options) {
    		super(options);
    		init(this, options, instance, create_fragment, safe_not_equal, { value: 0 });

    		dispatch_dev("SvelteRegisterComponent", {
    			component: this,
    			tagName: "Loading",
    			options,
    			id: create_fragment.name
    		});
    	}

    	get value() {
    		throw new Error("<Loading>: Props cannot be read directly from the component instance unless compiling with 'accessors: true' or '<svelte:options accessors/>'");
    	}

    	set value(value) {
    		throw new Error("<Loading>: Props cannot be set directly on the component instance unless compiling with 'accessors: true' or '<svelte:options accessors/>'");
    	}
    }

    /* src\components\Settings.svelte generated by Svelte v3.38.2 */

    const file$1 = "src\\components\\Settings.svelte";

    function create_fragment$1(ctx) {
    	let div14;
    	let div13;
    	let div1;
    	let div0;
    	let t1;
    	let nav;
    	let div2;
    	let button0;
    	let t3;
    	let button1;
    	let t5;
    	let form;
    	let div5;
    	let div3;
    	let label0;
    	let t7;
    	let div4;
    	let input0;
    	let t8;
    	let div8;
    	let div6;
    	let label1;
    	let t10;
    	let div7;
    	let input1;
    	let t11;
    	let div11;
    	let div9;
    	let label2;
    	let t13;
    	let div10;
    	let textarea;
    	let t14;
    	let div12;
    	let button2;
    	let t16;
    	let button3;

    	const block = {
    		c: function create() {
    			div14 = element("div");
    			div13 = element("div");
    			div1 = element("div");
    			div0 = element("div");
    			div0.textContent = "Preferences";
    			t1 = space();
    			nav = element("nav");
    			div2 = element("div");
    			button0 = element("button");
    			button0.textContent = "General";
    			t3 = space();
    			button1 = element("button");
    			button1.textContent = "DoT/HoT Tracker";
    			t5 = space();
    			form = element("form");
    			div5 = element("div");
    			div3 = element("div");
    			label0 = element("label");
    			label0.textContent = "Name";
    			t7 = space();
    			div4 = element("div");
    			input0 = element("input");
    			t8 = space();
    			div8 = element("div");
    			div6 = element("div");
    			label1 = element("label");
    			label1.textContent = "Ability ID";
    			t10 = space();
    			div7 = element("div");
    			input1 = element("input");
    			t11 = space();
    			div11 = element("div");
    			div9 = element("div");
    			label2 = element("label");
    			label2.textContent = "Example Textarea";
    			t13 = space();
    			div10 = element("div");
    			textarea = element("textarea");
    			t14 = space();
    			div12 = element("div");
    			button2 = element("button");
    			button2.textContent = "Save changes";
    			t16 = space();
    			button3 = element("button");
    			button3.textContent = "Cancel";
    			attr_dev(div0, "class", "Subhead-heading");
    			add_location(div0, file$1, 4, 3, 127);
    			attr_dev(div1, "class", "Subhead border-0");
    			add_location(div1, file$1, 3, 2, 93);
    			attr_dev(button0, "class", "UnderlineNav-item");
    			attr_dev(button0, "role", "tab");
    			attr_dev(button0, "type", "button");
    			add_location(button0, file$1, 9, 4, 264);
    			attr_dev(button1, "class", "UnderlineNav-item");
    			attr_dev(button1, "role", "tab");
    			attr_dev(button1, "type", "button");
    			attr_dev(button1, "aria-selected", "true");
    			add_location(button1, file$1, 10, 4, 344);
    			attr_dev(div2, "class", "UnderlineNav-body");
    			attr_dev(div2, "role", "menu");
    			add_location(div2, file$1, 8, 3, 216);
    			attr_dev(nav, "class", "UnderlineNav");
    			add_location(nav, file$1, 7, 2, 186);
    			attr_dev(label0, "for", "tracker-name");
    			add_location(label0, file$1, 17, 5, 547);
    			attr_dev(div3, "class", "form-group-header");
    			add_location(div3, file$1, 16, 4, 510);
    			attr_dev(input0, "class", "form-control");
    			attr_dev(input0, "type", "text");
    			attr_dev(input0, "placeholder", "My Affliction Tracker");
    			attr_dev(input0, "id", "tracker-name");
    			add_location(input0, file$1, 20, 5, 636);
    			attr_dev(div4, "class", "form-group-body");
    			add_location(div4, file$1, 19, 4, 601);
    			attr_dev(div5, "class", "form-group");
    			add_location(div5, file$1, 15, 3, 481);
    			attr_dev(label1, "for", "ability-id");
    			add_location(label1, file$1, 26, 5, 824);
    			attr_dev(div6, "class", "form-group-header");
    			add_location(div6, file$1, 25, 4, 787);
    			attr_dev(input1, "class", "form-control");
    			attr_dev(input1, "type", "text");
    			attr_dev(input1, "placeholder", "808192586022912");
    			attr_dev(input1, "id", "ability-id");
    			add_location(input1, file$1, 29, 5, 917);
    			attr_dev(div7, "class", "form-group-body");
    			add_location(div7, file$1, 28, 4, 882);
    			attr_dev(div8, "class", "form-group");
    			add_location(div8, file$1, 24, 3, 758);
    			attr_dev(label2, "for", "example-textarea");
    			add_location(label2, file$1, 35, 5, 1097);
    			attr_dev(div9, "class", "form-group-header");
    			add_location(div9, file$1, 34, 4, 1060);
    			attr_dev(textarea, "class", "form-control");
    			attr_dev(textarea, "id", "example-textarea");
    			add_location(textarea, file$1, 38, 5, 1202);
    			attr_dev(div10, "class", "form-group-body");
    			add_location(div10, file$1, 37, 4, 1167);
    			attr_dev(div11, "class", "form-group");
    			add_location(div11, file$1, 33, 3, 1031);
    			attr_dev(button2, "type", "submit");
    			attr_dev(button2, "class", "btn btn-primary");
    			add_location(button2, file$1, 43, 4, 1314);
    			attr_dev(button3, "type", "button");
    			attr_dev(button3, "class", "btn");
    			add_location(button3, file$1, 44, 4, 1386);
    			attr_dev(div12, "class", "form-actions");
    			add_location(div12, file$1, 42, 3, 1283);
    			add_location(form, file$1, 14, 2, 471);
    			attr_dev(div13, "class", "Layout-main");
    			add_location(div13, file$1, 1, 1, 46);
    			attr_dev(div14, "class", "Layout Layout--gutter-none p-4");
    			add_location(div14, file$1, 0, 0, 0);
    		},
    		l: function claim(nodes) {
    			throw new Error("options.hydrate only works if the component was compiled with the `hydratable: true` option");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div14, anchor);
    			append_dev(div14, div13);
    			append_dev(div13, div1);
    			append_dev(div1, div0);
    			append_dev(div13, t1);
    			append_dev(div13, nav);
    			append_dev(nav, div2);
    			append_dev(div2, button0);
    			append_dev(div2, t3);
    			append_dev(div2, button1);
    			append_dev(div13, t5);
    			append_dev(div13, form);
    			append_dev(form, div5);
    			append_dev(div5, div3);
    			append_dev(div3, label0);
    			append_dev(div5, t7);
    			append_dev(div5, div4);
    			append_dev(div4, input0);
    			append_dev(form, t8);
    			append_dev(form, div8);
    			append_dev(div8, div6);
    			append_dev(div6, label1);
    			append_dev(div8, t10);
    			append_dev(div8, div7);
    			append_dev(div7, input1);
    			append_dev(form, t11);
    			append_dev(form, div11);
    			append_dev(div11, div9);
    			append_dev(div9, label2);
    			append_dev(div11, t13);
    			append_dev(div11, div10);
    			append_dev(div10, textarea);
    			append_dev(form, t14);
    			append_dev(form, div12);
    			append_dev(div12, button2);
    			append_dev(div12, t16);
    			append_dev(div12, button3);
    		},
    		p: noop,
    		i: noop,
    		o: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div14);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_fragment$1.name,
    		type: "component",
    		source: "",
    		ctx
    	});

    	return block;
    }

    function instance$1($$self, $$props) {
    	let { $$slots: slots = {}, $$scope } = $$props;
    	validate_slots("Settings", slots, []);
    	const writable_props = [];

    	Object.keys($$props).forEach(key => {
    		if (!~writable_props.indexOf(key) && key.slice(0, 2) !== "$$") console.warn(`<Settings> was created with unknown prop '${key}'`);
    	});

    	return [];
    }

    class Settings extends SvelteComponentDev {
    	constructor(options) {
    		super(options);
    		init(this, options, instance$1, create_fragment$1, safe_not_equal, {});

    		dispatch_dev("SvelteRegisterComponent", {
    			component: this,
    			tagName: "Settings",
    			options,
    			id: create_fragment$1.name
    		});
    	}
    }

    /* src\main_menu\App.svelte generated by Svelte v3.38.2 */

    const { Object: Object_1, console: console_1 } = globals;
    const file$2 = "src\\main_menu\\App.svelte";

    function get_each_context_5(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[66] = list[i];
    	return child_ctx;
    }

    function get_each_context_2(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[54] = list[i];
    	return child_ctx;
    }

    function get_each_context_3(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[54] = list[i];
    	return child_ctx;
    }

    function get_each_context(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[54] = list[i];
    	return child_ctx;
    }

    function get_each_context_1(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[54] = list[i];
    	return child_ctx;
    }

    function get_each_context_4(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[63] = list[i];
    	return child_ctx;
    }

    function get_each_context_6(ctx, list, i) {
    	const child_ctx = ctx.slice();
    	child_ctx[66] = list[i];
    	return child_ctx;
    }

    // (1110:0) {#if loading}
    function create_if_block_13(ctx) {
    	let loading_1;
    	let updating_value;
    	let current;

    	function loading_1_value_binding(value) {
    		/*loading_1_value_binding*/ ctx[16](value);
    	}

    	let loading_1_props = {};

    	if (/*percentage*/ ctx[8] !== void 0) {
    		loading_1_props.value = /*percentage*/ ctx[8];
    	}

    	loading_1 = new Loading({ props: loading_1_props, $$inline: true });
    	binding_callbacks.push(() => bind(loading_1, "value", loading_1_value_binding));

    	const block = {
    		c: function create() {
    			create_component(loading_1.$$.fragment);
    		},
    		m: function mount(target, anchor) {
    			mount_component(loading_1, target, anchor);
    			current = true;
    		},
    		p: function update(ctx, dirty) {
    			const loading_1_changes = {};

    			if (!updating_value && dirty[0] & /*percentage*/ 256) {
    				updating_value = true;
    				loading_1_changes.value = /*percentage*/ ctx[8];
    				add_flush_callback(() => updating_value = false);
    			}

    			loading_1.$set(loading_1_changes);
    		},
    		i: function intro(local) {
    			if (current) return;
    			transition_in(loading_1.$$.fragment, local);
    			current = true;
    		},
    		o: function outro(local) {
    			transition_out(loading_1.$$.fragment, local);
    			current = false;
    		},
    		d: function destroy(detaching) {
    			destroy_component(loading_1, detaching);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_13.name,
    		type: "if",
    		source: "(1110:0) {#if loading}",
    		ctx
    	});

    	return block;
    }

    // (2050:0) {:else}
    function create_else_block_4(ctx) {
    	let div2;
    	let div1;
    	let div0;
    	let img;
    	let img_src_value;
    	let t0;
    	let h3;
    	let t2;
    	let p;
    	let t4;
    	let button;
    	let t5;
    	let mounted;
    	let dispose;

    	const block = {
    		c: function create() {
    			div2 = element("div");
    			div1 = element("div");
    			div0 = element("div");
    			img = element("img");
    			t0 = space();
    			h3 = element("h3");
    			h3.textContent = "Welcome to Trace Parse!";
    			t2 = space();
    			p = element("p");
    			p.textContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu urna laoreet, euismod mi vel, porttitor magna.\n\t\t\t\t\tNam accumsan, urna sed pellentesque tempor, leo nunc iaculis nibh, non lacinia lectus quam nec eros. Phasellus\n\t\t\t\t\ta congue quam. Sed malesuada quam non odio fringilla mollis.";
    			t4 = space();
    			button = element("button");
    			t5 = text("Parse");
    			if (img.src !== (img_src_value = "logo.svg")) attr_dev(img, "src", img_src_value);
    			add_location(img, file$2, 2053, 4, 64166);
    			attr_dev(h3, "class", "mb-1 h1");
    			add_location(h3, file$2, 2054, 4, 64193);
    			add_location(p, file$2, 2055, 4, 64246);
    			attr_dev(button, "class", "btn btn-outline my-3");
    			attr_dev(button, "type", "button");
    			attr_dev(button, "aria-selected", /*realTimeParse*/ ctx[4]);
    			add_location(button, file$2, 2061, 4, 64562);
    			attr_dev(div0, "class", "blankslate blankslate-narrow");
    			add_location(div0, file$2, 2052, 3, 64119);
    			attr_dev(div1, "class", "Layout-main");
    			add_location(div1, file$2, 2051, 2, 64090);
    			attr_dev(div2, "class", "Layout Layout--sidebarPosition-flowRow-end Layout--gutter-none");
    			add_location(div2, file$2, 2050, 1, 64011);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div2, anchor);
    			append_dev(div2, div1);
    			append_dev(div1, div0);
    			append_dev(div0, img);
    			append_dev(div0, t0);
    			append_dev(div0, h3);
    			append_dev(div0, t2);
    			append_dev(div0, p);
    			append_dev(div0, t4);
    			append_dev(div0, button);
    			append_dev(button, t5);

    			if (!mounted) {
    				dispose = listen_dev(button, "click", /*toggleParse*/ ctx[14], false, false, false);
    				mounted = true;
    			}
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*realTimeParse*/ 16) {
    				attr_dev(button, "aria-selected", /*realTimeParse*/ ctx[4]);
    			}
    		},
    		i: noop,
    		o: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div2);
    			mounted = false;
    			dispose();
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_else_block_4.name,
    		type: "else",
    		source: "(2050:0) {:else}",
    		ctx
    	});

    	return block;
    }

    // (2048:38) 
    function create_if_block_12(ctx) {
    	let settings;
    	let current;
    	settings = new Settings({ $$inline: true });

    	const block = {
    		c: function create() {
    			create_component(settings.$$.fragment);
    		},
    		m: function mount(target, anchor) {
    			mount_component(settings, target, anchor);
    			current = true;
    		},
    		p: noop,
    		i: function intro(local) {
    			if (current) return;
    			transition_in(settings.$$.fragment, local);
    			current = true;
    		},
    		o: function outro(local) {
    			transition_out(settings.$$.fragment, local);
    			current = false;
    		},
    		d: function destroy(detaching) {
    			destroy_component(settings, detaching);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_12.name,
    		type: "if",
    		source: "(2048:38) ",
    		ctx
    	});

    	return block;
    }

    // (1146:0) {#if selectedMenu === 'analyze'}
    function create_if_block(ctx) {
    	let div6;
    	let div5;
    	let div2;
    	let div0;
    	let t1;
    	let t2;
    	let div1;
    	let t4;
    	let div4;
    	let div3;
    	let if_block0 = /*noFileSelected*/ ctx[9] && create_if_block_11(ctx);

    	function select_block_type_1(ctx, dirty) {
    		if (typeof /*combat*/ ctx[5] !== "undefined") return create_if_block_1;
    		if (!/*noFileSelected*/ ctx[9]) return create_if_block_10;
    	}

    	let current_block_type = select_block_type_1(ctx);
    	let if_block1 = current_block_type && current_block_type(ctx);

    	const block = {
    		c: function create() {
    			div6 = element("div");
    			div5 = element("div");
    			div2 = element("div");
    			div0 = element("div");
    			div0.textContent = "Analyze Logs";
    			t1 = space();
    			if (if_block0) if_block0.c();
    			t2 = space();
    			div1 = element("div");
    			div1.textContent = "Get statistical information from your combat encounters.";
    			t4 = space();
    			div4 = element("div");
    			div3 = element("div");
    			if (if_block1) if_block1.c();
    			attr_dev(div0, "class", "Subhead-heading");
    			add_location(div0, file$2, 1150, 4, 33160);
    			attr_dev(div1, "class", "Subhead-description");
    			add_location(div1, file$2, 1172, 4, 33808);
    			attr_dev(div2, "class", "Subhead");
    			add_location(div2, file$2, 1149, 3, 33134);
    			attr_dev(div3, "class", "mt-4");
    			add_location(div3, file$2, 1177, 4, 33966);
    			attr_dev(div4, "class", "Layout-main");
    			add_location(div4, file$2, 1176, 3, 33936);
    			attr_dev(div5, "class", "Layout-main");
    			add_location(div5, file$2, 1147, 2, 33085);
    			attr_dev(div6, "class", "Layout Layout--gutter-none p-4");
    			add_location(div6, file$2, 1146, 1, 33038);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div6, anchor);
    			append_dev(div6, div5);
    			append_dev(div5, div2);
    			append_dev(div2, div0);
    			append_dev(div2, t1);
    			if (if_block0) if_block0.m(div2, null);
    			append_dev(div2, t2);
    			append_dev(div2, div1);
    			append_dev(div5, t4);
    			append_dev(div5, div4);
    			append_dev(div4, div3);
    			if (if_block1) if_block1.m(div3, null);
    		},
    		p: function update(ctx, dirty) {
    			if (/*noFileSelected*/ ctx[9]) {
    				if (if_block0) {
    					if_block0.p(ctx, dirty);
    				} else {
    					if_block0 = create_if_block_11(ctx);
    					if_block0.c();
    					if_block0.m(div2, t2);
    				}
    			} else if (if_block0) {
    				if_block0.d(1);
    				if_block0 = null;
    			}

    			if (current_block_type === (current_block_type = select_block_type_1(ctx)) && if_block1) {
    				if_block1.p(ctx, dirty);
    			} else {
    				if (if_block1) if_block1.d(1);
    				if_block1 = current_block_type && current_block_type(ctx);

    				if (if_block1) {
    					if_block1.c();
    					if_block1.m(div3, null);
    				}
    			}
    		},
    		i: noop,
    		o: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div6);
    			if (if_block0) if_block0.d();

    			if (if_block1) {
    				if_block1.d();
    			}
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block.name,
    		type: "if",
    		source: "(1146:0) {#if selectedMenu === 'analyze'}",
    		ctx
    	});

    	return block;
    }

    // (1152:4) {#if noFileSelected}
    function create_if_block_11(ctx) {
    	let div1;
    	let div0;
    	let form;
    	let select;
    	let option;
    	let mounted;
    	let dispose;
    	let each_value_6 = /*files*/ ctx[2];
    	validate_each_argument(each_value_6);
    	let each_blocks = [];

    	for (let i = 0; i < each_value_6.length; i += 1) {
    		each_blocks[i] = create_each_block_6(get_each_context_6(ctx, each_value_6, i));
    	}

    	const block = {
    		c: function create() {
    			div1 = element("div");
    			div0 = element("div");
    			form = element("form");
    			select = element("select");
    			option = element("option");
    			option.textContent = "Select a combat log";

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].c();
    			}

    			attr_dev(option, "values", "");
    			option.__value = "Select a combat log";
    			option.value = option.__value;
    			add_location(option, file$2, 1162, 9, 33568);
    			attr_dev(select, "class", "form-select");
    			attr_dev(select, "aria-label", "Combat logs");
    			select.disabled = /*realTimeParse*/ ctx[4];
    			if (/*selectedFile*/ ctx[3] === void 0) add_render_callback(() => /*select_change_handler*/ ctx[19].call(select));
    			add_location(select, file$2, 1155, 8, 33352);
    			add_location(form, file$2, 1154, 7, 33337);
    			attr_dev(div0, "class", "d-flex flex-justify-end position-relative");
    			add_location(div0, file$2, 1153, 6, 33274);
    			attr_dev(div1, "class", "Subhead-actions");
    			add_location(div1, file$2, 1152, 5, 33238);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div1, anchor);
    			append_dev(div1, div0);
    			append_dev(div0, form);
    			append_dev(form, select);
    			append_dev(select, option);

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].m(select, null);
    			}

    			select_option(select, /*selectedFile*/ ctx[3]);

    			if (!mounted) {
    				dispose = [
    					listen_dev(select, "change", /*select_change_handler*/ ctx[19]),
    					listen_dev(select, "blur", /*selectCombat*/ ctx[12], false, false, false),
    					listen_dev(select, "change", /*selectCombat*/ ctx[12], false, false, false)
    				];

    				mounted = true;
    			}
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*files, interpretCombatFileName*/ 32772) {
    				each_value_6 = /*files*/ ctx[2];
    				validate_each_argument(each_value_6);
    				let i;

    				for (i = 0; i < each_value_6.length; i += 1) {
    					const child_ctx = get_each_context_6(ctx, each_value_6, i);

    					if (each_blocks[i]) {
    						each_blocks[i].p(child_ctx, dirty);
    					} else {
    						each_blocks[i] = create_each_block_6(child_ctx);
    						each_blocks[i].c();
    						each_blocks[i].m(select, null);
    					}
    				}

    				for (; i < each_blocks.length; i += 1) {
    					each_blocks[i].d(1);
    				}

    				each_blocks.length = each_value_6.length;
    			}

    			if (dirty[0] & /*realTimeParse*/ 16) {
    				prop_dev(select, "disabled", /*realTimeParse*/ ctx[4]);
    			}

    			if (dirty[0] & /*selectedFile, files*/ 12) {
    				select_option(select, /*selectedFile*/ ctx[3]);
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div1);
    			destroy_each(each_blocks, detaching);
    			mounted = false;
    			run_all(dispose);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_11.name,
    		type: "if",
    		source: "(1152:4) {#if noFileSelected}",
    		ctx
    	});

    	return block;
    }

    // (1165:9) {#each files as file}
    function create_each_block_6(ctx) {
    	let option;
    	let t_value = /*interpretCombatFileName*/ ctx[15](/*file*/ ctx[66]) + "";
    	let t;
    	let option_value_value;

    	const block = {
    		c: function create() {
    			option = element("option");
    			t = text(t_value);
    			option.__value = option_value_value = /*file*/ ctx[66];
    			option.value = option.__value;
    			add_location(option, file$2, 1165, 10, 33657);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, option, anchor);
    			append_dev(option, t);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*files*/ 4 && t_value !== (t_value = /*interpretCombatFileName*/ ctx[15](/*file*/ ctx[66]) + "")) set_data_dev(t, t_value);

    			if (dirty[0] & /*files*/ 4 && option_value_value !== (option_value_value = /*file*/ ctx[66])) {
    				prop_dev(option, "__value", option_value_value);
    				option.value = option.__value;
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(option);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block_6.name,
    		type: "each",
    		source: "(1165:9) {#each files as file}",
    		ctx
    	});

    	return block;
    }

    // (2019:31) 
    function create_if_block_10(ctx) {
    	let div;
    	let h3;
    	let t1;
    	let p;
    	let t3;
    	let form;
    	let select;
    	let option;
    	let mounted;
    	let dispose;
    	let each_value_5 = /*files*/ ctx[2];
    	validate_each_argument(each_value_5);
    	let each_blocks = [];

    	for (let i = 0; i < each_value_5.length; i += 1) {
    		each_blocks[i] = create_each_block_5(get_each_context_5(ctx, each_value_5, i));
    	}

    	const block = {
    		c: function create() {
    			div = element("div");
    			h3 = element("h3");
    			h3.textContent = "Trace your combats!";
    			t1 = space();
    			p = element("p");
    			p.textContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu urna laoreet, euismod mi vel, porttitor\n\t\t\t\t\t\t\t\tmagna.";
    			t3 = space();
    			form = element("form");
    			select = element("select");
    			option = element("option");
    			option.textContent = "Select a combat log";

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].c();
    			}

    			attr_dev(h3, "class", "mb-1 h1");
    			add_location(h3, file$2, 2020, 7, 63252);
    			add_location(p, file$2, 2021, 7, 63304);
    			attr_dev(option, "values", "");
    			option.__value = "Select a combat log";
    			option.value = option.__value;
    			add_location(option, file$2, 2034, 9, 63686);
    			attr_dev(select, "class", "form-select");
    			attr_dev(select, "aria-label", "Combat logs");
    			select.disabled = /*realTimeParse*/ ctx[4];
    			if (/*selectedFile*/ ctx[3] === void 0) add_render_callback(() => /*select_change_handler_1*/ ctx[23].call(select));
    			add_location(select, file$2, 2027, 8, 63470);
    			add_location(form, file$2, 2026, 7, 63455);
    			attr_dev(div, "class", "blankslate blankslate-narrow");
    			add_location(div, file$2, 2019, 6, 63202);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div, anchor);
    			append_dev(div, h3);
    			append_dev(div, t1);
    			append_dev(div, p);
    			append_dev(div, t3);
    			append_dev(div, form);
    			append_dev(form, select);
    			append_dev(select, option);

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].m(select, null);
    			}

    			select_option(select, /*selectedFile*/ ctx[3]);

    			if (!mounted) {
    				dispose = [
    					listen_dev(select, "change", /*select_change_handler_1*/ ctx[23]),
    					listen_dev(select, "blur", /*selectCombat*/ ctx[12], false, false, false),
    					listen_dev(select, "change", /*selectCombat*/ ctx[12], false, false, false)
    				];

    				mounted = true;
    			}
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*files, interpretCombatFileName*/ 32772) {
    				each_value_5 = /*files*/ ctx[2];
    				validate_each_argument(each_value_5);
    				let i;

    				for (i = 0; i < each_value_5.length; i += 1) {
    					const child_ctx = get_each_context_5(ctx, each_value_5, i);

    					if (each_blocks[i]) {
    						each_blocks[i].p(child_ctx, dirty);
    					} else {
    						each_blocks[i] = create_each_block_5(child_ctx);
    						each_blocks[i].c();
    						each_blocks[i].m(select, null);
    					}
    				}

    				for (; i < each_blocks.length; i += 1) {
    					each_blocks[i].d(1);
    				}

    				each_blocks.length = each_value_5.length;
    			}

    			if (dirty[0] & /*realTimeParse*/ 16) {
    				prop_dev(select, "disabled", /*realTimeParse*/ ctx[4]);
    			}

    			if (dirty[0] & /*selectedFile, files*/ 12) {
    				select_option(select, /*selectedFile*/ ctx[3]);
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div);
    			destroy_each(each_blocks, detaching);
    			mounted = false;
    			run_all(dispose);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_10.name,
    		type: "if",
    		source: "(2019:31) ",
    		ctx
    	});

    	return block;
    }

    // (1179:5) {#if typeof combat !== 'undefined'}
    function create_if_block_1(ctx) {
    	let div6;
    	let div2;
    	let div0;
    	let t0_value = /*combat*/ ctx[5].battle.replace(/\(|\)/g, "") + "";
    	let t0;
    	let t1;
    	let div1;
    	let t2;
    	let p0;
    	let t3;
    	let t4_value = [...new Set(/*combat*/ ctx[5].targets.map(func_3))].join(", ") + "";
    	let t4;
    	let t5;
    	let p1;
    	let t6;
    	let t7_value = /*moment*/ ctx[0](/*combat*/ ctx[5].start, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "";
    	let t7;
    	let t8;
    	let t9_value = /*moment*/ ctx[0](/*combat*/ ctx[5].end, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "";
    	let t9;
    	let t10;
    	let details;
    	let summary;
    	let t11_value = /*combat*/ ctx[5].player.replace("@", "") + "";
    	let t11;
    	let t12;
    	let t13_value = /*combat*/ ctx[5].battle.replace(/\(|\)/g, "") + "";
    	let t13;
    	let t14;
    	let br;
    	let t15;
    	let span0;
    	let t16_value = /*moment*/ ctx[0](/*combat*/ ctx[5].start, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "";
    	let t16;
    	let t17;
    	let t18;
    	let span1;
    	let t19_value = /*moment*/ ctx[0](/*combat*/ ctx[5].end, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "";
    	let t19;
    	let t20;
    	let t21;
    	let span2;
    	let t22_value = /*combat*/ ctx[5].duration + "";
    	let t22;
    	let t23;
    	let div5;
    	let div4;
    	let div3;
    	let show_if = Array.isArray(/*logs*/ ctx[7][/*selectedFile*/ ctx[3]]);
    	let t24;
    	let div11;
    	let div7;
    	let p2;
    	let t25_value = /*combat*/ ctx[5].duration + "";
    	let t25;
    	let t26;
    	let p3;
    	let t28;
    	let div8;
    	let p4;
    	let t29_value = /*combat*/ ctx[5].apm.toFixed(2) + "";
    	let t29;
    	let t30;
    	let p5;
    	let t32;
    	let div9;
    	let p6;
    	let t33_value = /*combat*/ ctx[5].hits + "";
    	let t33;
    	let t34;
    	let p7;
    	let t36;
    	let div10;
    	let p8;

    	let t37_value = /*combat*/ ctx[5].gcdMedian.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t37;
    	let t38;
    	let p9;
    	let t40;
    	let div24;
    	let div17;
    	let div14;
    	let div12;
    	let p10;

    	let t41_value = /*combat*/ ctx[5].damage.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t41;
    	let t42;
    	let p11;
    	let t44;
    	let div13;
    	let p12;

    	let t45_value = /*combat*/ ctx[5].dps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t45;
    	let t46;
    	let p13;
    	let t48;
    	let div16;
    	let div15;
    	let t49;
    	let div23;
    	let div20;
    	let div18;
    	let p14;

    	let t50_value = /*combat*/ ctx[5].heals.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t50;
    	let t51;
    	let p15;
    	let t53;
    	let div19;
    	let p16;

    	let t54_value = /*combat*/ ctx[5].hps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t54;
    	let t55;
    	let p17;
    	let t57;
    	let div22;
    	let div21;
    	let t58;
    	let div37;
    	let div30;
    	let div27;
    	let div25;
    	let p18;

    	let t59_value = /*combat*/ ctx[5].damageTaken.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t59;
    	let t60;
    	let p19;
    	let t62;
    	let div26;
    	let p20;

    	let t63_value = /*combat*/ ctx[5].dtps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t63;
    	let t64;
    	let p21;
    	let t66;
    	let div29;
    	let div28;
    	let t67;
    	let div36;
    	let div33;
    	let div31;
    	let p22;

    	let t68_value = /*combat*/ ctx[5].threat.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t68;
    	let t69;
    	let p23;
    	let t71;
    	let div32;
    	let p24;

    	let t72_value = /*combat*/ ctx[5].tps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t72;
    	let t73;
    	let p25;
    	let t75;
    	let div35;
    	let div34;
    	let t76;
    	let div40;
    	let div39;
    	let div38;
    	let t77;
    	let div46;
    	let div41;
    	let p26;
    	let t78_value = /*combat*/ ctx[5].gcds.length + "";
    	let t78;
    	let t79;
    	let p27;
    	let t81;
    	let div42;
    	let p28;

    	let t82_value = /*combat*/ ctx[5].gcdMedian.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t82;
    	let t83;
    	let p29;
    	let t85;
    	let div43;
    	let p30;

    	let t86_value = /*combat*/ ctx[5].gcdMean.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t86;
    	let t87;
    	let p31;
    	let t89;
    	let div44;
    	let p32;

    	let t90_value = /*combat*/ ctx[5].gcdMin.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t90;
    	let t91;
    	let p33;
    	let t93;
    	let div45;
    	let p34;

    	let t94_value = /*combat*/ ctx[5].gcdMax.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t94;
    	let t95;
    	let p35;
    	let t97;
    	let div49;
    	let div48;
    	let div47;
    	let t98;
    	let div56;
    	let div52;
    	let div51;
    	let div50;
    	let t99;
    	let div55;
    	let div54;
    	let div53;
    	let t100;
    	let div58;
    	let nav;
    	let div57;
    	let button0;
    	let t101;
    	let button0_aria_selected_value;
    	let t102;
    	let button1;
    	let t103;
    	let button1_aria_selected_value;
    	let t104;
    	let mounted;
    	let dispose;
    	let if_block0 = /*combat*/ ctx[5].health !== null && create_if_block_9(ctx);
    	let if_block1 = show_if && create_if_block_8(ctx);

    	function select_block_type_2(ctx, dirty) {
    		if (/*selectedTab*/ ctx[10] === "damage") return create_if_block_2;
    		return create_else_block;
    	}

    	let current_block_type = select_block_type_2(ctx);
    	let if_block2 = current_block_type(ctx);

    	const block = {
    		c: function create() {
    			div6 = element("div");
    			div2 = element("div");
    			div0 = element("div");
    			t0 = text(t0_value);
    			t1 = space();
    			div1 = element("div");
    			if (if_block0) if_block0.c();
    			t2 = space();
    			p0 = element("p");
    			t3 = text("Targets: ");
    			t4 = text(t4_value);
    			t5 = space();
    			p1 = element("p");
    			t6 = text("Start: ");
    			t7 = text(t7_value);
    			t8 = text("\n\t\t\t\t\t\t\t\t\t\tEnded: ");
    			t9 = text(t9_value);
    			t10 = space();
    			details = element("details");
    			summary = element("summary");
    			t11 = text(t11_value);
    			t12 = text(" (");
    			t13 = text(t13_value);
    			t14 = text(")\n\t\t\t\t\t\t\t\t\t");
    			br = element("br");
    			t15 = space();
    			span0 = element("span");
    			t16 = text(t16_value);
    			t17 = text(" -");
    			t18 = space();
    			span1 = element("span");
    			t19 = text(t19_value);
    			t20 = text(" -");
    			t21 = space();
    			span2 = element("span");
    			t22 = text(t22_value);
    			t23 = space();
    			div5 = element("div");
    			div4 = element("div");
    			div3 = element("div");
    			if (if_block1) if_block1.c();
    			t24 = space();
    			div11 = element("div");
    			div7 = element("div");
    			p2 = element("p");
    			t25 = text(t25_value);
    			t26 = space();
    			p3 = element("p");
    			p3.textContent = "Duration";
    			t28 = space();
    			div8 = element("div");
    			p4 = element("p");
    			t29 = text(t29_value);
    			t30 = space();
    			p5 = element("p");
    			p5.textContent = "APM";
    			t32 = space();
    			div9 = element("div");
    			p6 = element("p");
    			t33 = text(t33_value);
    			t34 = space();
    			p7 = element("p");
    			p7.textContent = "Hits";
    			t36 = space();
    			div10 = element("div");
    			p8 = element("p");
    			t37 = text(t37_value);
    			t38 = space();
    			p9 = element("p");
    			p9.textContent = "GCD Median";
    			t40 = space();
    			div24 = element("div");
    			div17 = element("div");
    			div14 = element("div");
    			div12 = element("div");
    			p10 = element("p");
    			t41 = text(t41_value);
    			t42 = space();
    			p11 = element("p");
    			p11.textContent = "Damage";
    			t44 = space();
    			div13 = element("div");
    			p12 = element("p");
    			t45 = text(t45_value);
    			t46 = space();
    			p13 = element("p");
    			p13.textContent = "DPS";
    			t48 = space();
    			div16 = element("div");
    			div15 = element("div");
    			t49 = space();
    			div23 = element("div");
    			div20 = element("div");
    			div18 = element("div");
    			p14 = element("p");
    			t50 = text(t50_value);
    			t51 = space();
    			p15 = element("p");
    			p15.textContent = "Heals";
    			t53 = space();
    			div19 = element("div");
    			p16 = element("p");
    			t54 = text(t54_value);
    			t55 = space();
    			p17 = element("p");
    			p17.textContent = "HPS";
    			t57 = space();
    			div22 = element("div");
    			div21 = element("div");
    			t58 = space();
    			div37 = element("div");
    			div30 = element("div");
    			div27 = element("div");
    			div25 = element("div");
    			p18 = element("p");
    			t59 = text(t59_value);
    			t60 = space();
    			p19 = element("p");
    			p19.textContent = "Damage Taken";
    			t62 = space();
    			div26 = element("div");
    			p20 = element("p");
    			t63 = text(t63_value);
    			t64 = space();
    			p21 = element("p");
    			p21.textContent = "DTPS";
    			t66 = space();
    			div29 = element("div");
    			div28 = element("div");
    			t67 = space();
    			div36 = element("div");
    			div33 = element("div");
    			div31 = element("div");
    			p22 = element("p");
    			t68 = text(t68_value);
    			t69 = space();
    			p23 = element("p");
    			p23.textContent = "Threat";
    			t71 = space();
    			div32 = element("div");
    			p24 = element("p");
    			t72 = text(t72_value);
    			t73 = space();
    			p25 = element("p");
    			p25.textContent = "TPS";
    			t75 = space();
    			div35 = element("div");
    			div34 = element("div");
    			t76 = space();
    			div40 = element("div");
    			div39 = element("div");
    			div38 = element("div");
    			t77 = space();
    			div46 = element("div");
    			div41 = element("div");
    			p26 = element("p");
    			t78 = text(t78_value);
    			t79 = space();
    			p27 = element("p");
    			p27.textContent = "GCDs Used";
    			t81 = space();
    			div42 = element("div");
    			p28 = element("p");
    			t82 = text(t82_value);
    			t83 = space();
    			p29 = element("p");
    			p29.textContent = "GCD Median";
    			t85 = space();
    			div43 = element("div");
    			p30 = element("p");
    			t86 = text(t86_value);
    			t87 = space();
    			p31 = element("p");
    			p31.textContent = "GCD Average";
    			t89 = space();
    			div44 = element("div");
    			p32 = element("p");
    			t90 = text(t90_value);
    			t91 = space();
    			p33 = element("p");
    			p33.textContent = "GCD Min";
    			t93 = space();
    			div45 = element("div");
    			p34 = element("p");
    			t94 = text(t94_value);
    			t95 = space();
    			p35 = element("p");
    			p35.textContent = "GCD Max";
    			t97 = space();
    			div49 = element("div");
    			div48 = element("div");
    			div47 = element("div");
    			t98 = space();
    			div56 = element("div");
    			div52 = element("div");
    			div51 = element("div");
    			div50 = element("div");
    			t99 = space();
    			div55 = element("div");
    			div54 = element("div");
    			div53 = element("div");
    			t100 = space();
    			div58 = element("div");
    			nav = element("nav");
    			div57 = element("div");
    			button0 = element("button");
    			t101 = text("Damage");
    			t102 = space();
    			button1 = element("button");
    			t103 = text("Heals");
    			t104 = space();
    			if_block2.c();
    			attr_dev(div0, "class", "h1 lh-condensed-ultra p-1");
    			add_location(div0, file$2, 1182, 8, 34145);
    			add_location(p0, file$2, 1190, 9, 34416);
    			add_location(p1, file$2, 1193, 9, 34554);
    			attr_dev(div1, "class", "f4 lh-condensed-ultra p-1");
    			add_location(div1, file$2, 1185, 8, 34255);
    			add_location(div2, file$2, 1181, 7, 34131);
    			add_location(br, file$2, 1203, 9, 34968);
    			attr_dev(span0, "class", "f6");
    			add_location(span0, file$2, 1204, 9, 34984);
    			attr_dev(span1, "class", "f6");
    			add_location(span1, file$2, 1205, 9, 35081);
    			attr_dev(span2, "class", "f6");
    			add_location(span2, file$2, 1206, 9, 35176);
    			attr_dev(summary, "class", "btn btn-large");
    			attr_dev(summary, "aria-haspopup", "true");
    			add_location(summary, file$2, 1201, 8, 34824);
    			attr_dev(div3, "class", "SelectMenu-list");
    			add_location(div3, file$2, 1210, 10, 35328);
    			attr_dev(div4, "class", "SelectMenu-modal");
    			add_location(div4, file$2, 1209, 9, 35287);
    			attr_dev(div5, "class", "SelectMenu right-0");
    			add_location(div5, file$2, 1208, 8, 35245);
    			attr_dev(details, "class", "details-reset details-overlay");
    			details.open = true;
    			add_location(details, file$2, 1200, 7, 34763);
    			attr_dev(div6, "class", "d-flex flex-justify-between flex-row flex-items-center");
    			add_location(div6, file$2, 1180, 6, 34055);
    			attr_dev(p2, "class", "h2 lh-condensed-ultra");
    			add_location(p2, file$2, 1247, 8, 37237);
    			attr_dev(p3, "class", "f4 lh-condensed-ultra");
    			add_location(p3, file$2, 1248, 8, 37300);
    			attr_dev(div7, "class", "text-center");
    			add_location(div7, file$2, 1246, 7, 37203);
    			attr_dev(p4, "class", "h2 lh-condensed-ultra");
    			add_location(p4, file$2, 1252, 8, 37402);
    			attr_dev(p5, "class", "f4 lh-condensed-ultra");
    			add_location(p5, file$2, 1253, 8, 37471);
    			attr_dev(div8, "class", "text-center");
    			add_location(div8, file$2, 1251, 7, 37368);
    			attr_dev(p6, "class", "h2 lh-condensed-ultra");
    			add_location(p6, file$2, 1257, 8, 37568);
    			attr_dev(p7, "class", "f4 lh-condensed-ultra");
    			add_location(p7, file$2, 1258, 8, 37627);
    			attr_dev(div9, "class", "text-center");
    			add_location(div9, file$2, 1256, 7, 37534);
    			attr_dev(p8, "class", "h2 lh-condensed-ultra");
    			add_location(p8, file$2, 1262, 8, 37725);
    			attr_dev(p9, "class", "f4 lh-condensed-ultra");
    			add_location(p9, file$2, 1268, 8, 37920);
    			attr_dev(div10, "class", "text-center");
    			add_location(div10, file$2, 1261, 7, 37691);
    			attr_dev(div11, "class", "mt-4 d-flex flex-row flex-justify-around color-bg-primary p-2 Box rounded-3");
    			add_location(div11, file$2, 1245, 6, 37106);
    			attr_dev(p10, "class", "h2 lh-condensed-ultra");
    			add_location(p10, file$2, 1277, 10, 38291);
    			attr_dev(p11, "class", "f4 lh-condensed-ultra");
    			add_location(p11, file$2, 1283, 10, 38495);
    			attr_dev(div12, "class", "text-center");
    			add_location(div12, file$2, 1276, 9, 38255);
    			attr_dev(p12, "class", "h2 lh-condensed-ultra");
    			add_location(p12, file$2, 1287, 10, 38601);
    			attr_dev(p13, "class", "f4 lh-condensed-ultra");
    			add_location(p13, file$2, 1293, 10, 38802);
    			attr_dev(div13, "class", "text-center");
    			add_location(div13, file$2, 1286, 9, 38565);
    			attr_dev(div14, "class", "Box d-flex flex-row flex-justify-around flex-items-center p-2");
    			add_location(div14, file$2, 1275, 8, 38170);
    			attr_dev(div15, "id", "chart-timeline");
    			attr_dev(div15, "class", "chart");
    			add_location(div15, file$2, 1298, 9, 38915);
    			attr_dev(div16, "class", "Box mt-4");
    			add_location(div16, file$2, 1297, 8, 38883);
    			attr_dev(div17, "class", "col-12 col-lg-6");
    			add_location(div17, file$2, 1274, 7, 38132);
    			attr_dev(p14, "class", "h2 lh-condensed-ultra");
    			add_location(p14, file$2, 1305, 10, 39161);
    			attr_dev(p15, "class", "f4 lh-condensed-ultra");
    			add_location(p15, file$2, 1311, 10, 39364);
    			attr_dev(div18, "class", "text-center");
    			add_location(div18, file$2, 1304, 9, 39125);
    			attr_dev(p16, "class", "h2 lh-condensed-ultra");
    			add_location(p16, file$2, 1315, 10, 39469);
    			attr_dev(p17, "class", "f4 lh-condensed-ultra");
    			add_location(p17, file$2, 1321, 10, 39670);
    			attr_dev(div19, "class", "text-center");
    			add_location(div19, file$2, 1314, 9, 39433);
    			attr_dev(div20, "class", "Box d-flex flex-row flex-justify-around flex-items-center p-2");
    			add_location(div20, file$2, 1303, 8, 39040);
    			attr_dev(div21, "id", "chart-timeline-heal");
    			attr_dev(div21, "class", "chart");
    			add_location(div21, file$2, 1326, 9, 39783);
    			attr_dev(div22, "class", "Box mt-4");
    			add_location(div22, file$2, 1325, 8, 39751);
    			attr_dev(div23, "class", "col-12 col-lg-6 ml-lg-4");
    			add_location(div23, file$2, 1302, 7, 38994);
    			attr_dev(div24, "class", "mt-4 d-flex flex-column flex-lg-row flex-items-center flex-lg-items-center");
    			add_location(div24, file$2, 1273, 6, 38036);
    			attr_dev(p18, "class", "h2 lh-condensed-ultra");
    			add_location(p18, file$2, 1336, 10, 40176);
    			attr_dev(p19, "class", "f4 lh-condensed-ultra");
    			add_location(p19, file$2, 1342, 10, 40385);
    			attr_dev(div25, "class", "text-center");
    			add_location(div25, file$2, 1335, 9, 40140);
    			attr_dev(p20, "class", "h2 lh-condensed-ultra");
    			add_location(p20, file$2, 1346, 10, 40497);
    			attr_dev(p21, "class", "f4 lh-condensed-ultra");
    			add_location(p21, file$2, 1352, 10, 40699);
    			attr_dev(div26, "class", "text-center");
    			add_location(div26, file$2, 1345, 9, 40461);
    			attr_dev(div27, "class", "Box d-flex flex-row flex-justify-around flex-items-center p-2");
    			add_location(div27, file$2, 1334, 8, 40055);
    			attr_dev(div28, "id", "chart-timeline-damage-taken");
    			attr_dev(div28, "class", "chart");
    			add_location(div28, file$2, 1357, 9, 40813);
    			attr_dev(div29, "class", "Box mt-4");
    			add_location(div29, file$2, 1356, 8, 40781);
    			attr_dev(div30, "class", "col-12 col-lg-6");
    			add_location(div30, file$2, 1333, 7, 40017);
    			attr_dev(p22, "class", "h2 lh-condensed-ultra");
    			add_location(p22, file$2, 1364, 10, 41072);
    			attr_dev(p23, "class", "f4 lh-condensed-ultra");
    			add_location(p23, file$2, 1370, 10, 41276);
    			attr_dev(div31, "class", "text-center");
    			add_location(div31, file$2, 1363, 9, 41036);
    			attr_dev(p24, "class", "h2 lh-condensed-ultra");
    			add_location(p24, file$2, 1374, 10, 41382);
    			attr_dev(p25, "class", "f4 lh-condensed-ultra");
    			add_location(p25, file$2, 1380, 10, 41583);
    			attr_dev(div32, "class", "text-center");
    			add_location(div32, file$2, 1373, 9, 41346);
    			attr_dev(div33, "class", "Box d-flex flex-row flex-justify-around flex-items-center p-2");
    			add_location(div33, file$2, 1362, 8, 40951);
    			attr_dev(div34, "id", "chart-timeline-threat");
    			attr_dev(div34, "class", "chart");
    			add_location(div34, file$2, 1385, 9, 41696);
    			attr_dev(div35, "class", "Box mt-4");
    			add_location(div35, file$2, 1384, 8, 41664);
    			attr_dev(div36, "class", "col-12 col-lg-6 ml-lg-4");
    			add_location(div36, file$2, 1361, 7, 40905);
    			attr_dev(div37, "class", "mt-4 d-flex flex-column flex-lg-row flex-items-center flex-lg-items-center");
    			add_location(div37, file$2, 1332, 6, 39921);
    			attr_dev(div38, "id", "chart-hits");
    			attr_dev(div38, "class", "chart");
    			add_location(div38, file$2, 1393, 8, 41879);
    			attr_dev(div39, "class", "Box col-12");
    			add_location(div39, file$2, 1392, 7, 41846);
    			attr_dev(div40, "class", "mt-4");
    			add_location(div40, file$2, 1391, 6, 41820);
    			attr_dev(p26, "class", "h2 lh-condensed-ultra");
    			add_location(p26, file$2, 1400, 8, 42109);
    			attr_dev(p27, "class", "f4 lh-condensed-ultra");
    			add_location(p27, file$2, 1401, 8, 42175);
    			attr_dev(div41, "class", "text-center");
    			add_location(div41, file$2, 1399, 7, 42075);
    			attr_dev(p28, "class", "h2 lh-condensed-ultra");
    			add_location(p28, file$2, 1405, 8, 42278);
    			attr_dev(p29, "class", "f4 lh-condensed-ultra");
    			add_location(p29, file$2, 1411, 8, 42473);
    			attr_dev(div42, "class", "text-center");
    			add_location(div42, file$2, 1404, 7, 42244);
    			attr_dev(p30, "class", "h2 lh-condensed-ultra");
    			add_location(p30, file$2, 1415, 8, 42577);
    			attr_dev(p31, "class", "f4 lh-condensed-ultra");
    			add_location(p31, file$2, 1421, 8, 42770);
    			attr_dev(div43, "class", "text-center");
    			add_location(div43, file$2, 1414, 7, 42543);
    			attr_dev(p32, "class", "h2 lh-condensed-ultra");
    			add_location(p32, file$2, 1425, 8, 42875);
    			attr_dev(p33, "class", "f4 lh-condensed-ultra");
    			add_location(p33, file$2, 1431, 8, 43067);
    			attr_dev(div44, "class", "text-center");
    			add_location(div44, file$2, 1424, 7, 42841);
    			attr_dev(p34, "class", "h2 lh-condensed-ultra");
    			add_location(p34, file$2, 1435, 8, 43168);
    			attr_dev(p35, "class", "f4 lh-condensed-ultra");
    			add_location(p35, file$2, 1441, 8, 43360);
    			attr_dev(div45, "class", "text-center");
    			add_location(div45, file$2, 1434, 7, 43134);
    			attr_dev(div46, "class", "mt-4 d-flex flex-row flex-justify-around color-bg-primary p-2 Box rounded-3");
    			add_location(div46, file$2, 1398, 6, 41978);
    			attr_dev(div47, "id", "chart-gcd-per-ability");
    			attr_dev(div47, "class", "chart");
    			add_location(div47, file$2, 1448, 8, 43529);
    			attr_dev(div48, "class", "Box col-12");
    			add_location(div48, file$2, 1447, 7, 43496);
    			attr_dev(div49, "class", "mt-4");
    			add_location(div49, file$2, 1446, 6, 43470);
    			attr_dev(div50, "id", "chart-gcd-timeline-dps");
    			attr_dev(div50, "class", "chart");
    			add_location(div50, file$2, 1456, 9, 43805);
    			attr_dev(div51, "class", "Box mt-4");
    			add_location(div51, file$2, 1455, 8, 43773);
    			attr_dev(div52, "class", "col-12 col-lg-6");
    			add_location(div52, file$2, 1454, 7, 43735);
    			attr_dev(div53, "id", "chart-gcd-timeline-hps");
    			attr_dev(div53, "class", "chart");
    			add_location(div53, file$2, 1462, 9, 43970);
    			attr_dev(div54, "class", "Box mt-4");
    			add_location(div54, file$2, 1461, 8, 43938);
    			attr_dev(div55, "class", "col-12 col-lg-6 ml-lg-4");
    			add_location(div55, file$2, 1460, 7, 43892);
    			attr_dev(div56, "class", "d-flex flex-column flex-lg-row flex-items-center flex-lg-items-center");
    			add_location(div56, file$2, 1453, 6, 43644);
    			attr_dev(button0, "class", "UnderlineNav-item");
    			attr_dev(button0, "role", "tab");
    			attr_dev(button0, "type", "button");
    			attr_dev(button0, "aria-selected", button0_aria_selected_value = /*selectedTab*/ ctx[10] === "damage");
    			add_location(button0, file$2, 1471, 9, 44225);
    			attr_dev(button1, "class", "UnderlineNav-item");
    			attr_dev(button1, "role", "tab");
    			attr_dev(button1, "type", "button");
    			attr_dev(button1, "aria-selected", button1_aria_selected_value = /*selectedTab*/ ctx[10] === "heal");
    			add_location(button1, file$2, 1479, 9, 44463);
    			attr_dev(div57, "class", "UnderlineNav-body");
    			attr_dev(div57, "role", "tablist");
    			add_location(div57, file$2, 1470, 8, 44169);
    			attr_dev(nav, "class", "UnderlineNav");
    			add_location(nav, file$2, 1469, 7, 44134);
    			attr_dev(div58, "class", "mt-4 border rounded-3");
    			add_location(div58, file$2, 1468, 6, 44091);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div6, anchor);
    			append_dev(div6, div2);
    			append_dev(div2, div0);
    			append_dev(div0, t0);
    			append_dev(div2, t1);
    			append_dev(div2, div1);
    			if (if_block0) if_block0.m(div1, null);
    			append_dev(div1, t2);
    			append_dev(div1, p0);
    			append_dev(p0, t3);
    			append_dev(p0, t4);
    			append_dev(div1, t5);
    			append_dev(div1, p1);
    			append_dev(p1, t6);
    			append_dev(p1, t7);
    			append_dev(p1, t8);
    			append_dev(p1, t9);
    			append_dev(div6, t10);
    			append_dev(div6, details);
    			append_dev(details, summary);
    			append_dev(summary, t11);
    			append_dev(summary, t12);
    			append_dev(summary, t13);
    			append_dev(summary, t14);
    			append_dev(summary, br);
    			append_dev(summary, t15);
    			append_dev(summary, span0);
    			append_dev(span0, t16);
    			append_dev(span0, t17);
    			append_dev(summary, t18);
    			append_dev(summary, span1);
    			append_dev(span1, t19);
    			append_dev(span1, t20);
    			append_dev(summary, t21);
    			append_dev(summary, span2);
    			append_dev(span2, t22);
    			append_dev(details, t23);
    			append_dev(details, div5);
    			append_dev(div5, div4);
    			append_dev(div4, div3);
    			if (if_block1) if_block1.m(div3, null);
    			insert_dev(target, t24, anchor);
    			insert_dev(target, div11, anchor);
    			append_dev(div11, div7);
    			append_dev(div7, p2);
    			append_dev(p2, t25);
    			append_dev(div7, t26);
    			append_dev(div7, p3);
    			append_dev(div11, t28);
    			append_dev(div11, div8);
    			append_dev(div8, p4);
    			append_dev(p4, t29);
    			append_dev(div8, t30);
    			append_dev(div8, p5);
    			append_dev(div11, t32);
    			append_dev(div11, div9);
    			append_dev(div9, p6);
    			append_dev(p6, t33);
    			append_dev(div9, t34);
    			append_dev(div9, p7);
    			append_dev(div11, t36);
    			append_dev(div11, div10);
    			append_dev(div10, p8);
    			append_dev(p8, t37);
    			append_dev(div10, t38);
    			append_dev(div10, p9);
    			insert_dev(target, t40, anchor);
    			insert_dev(target, div24, anchor);
    			append_dev(div24, div17);
    			append_dev(div17, div14);
    			append_dev(div14, div12);
    			append_dev(div12, p10);
    			append_dev(p10, t41);
    			append_dev(div12, t42);
    			append_dev(div12, p11);
    			append_dev(div14, t44);
    			append_dev(div14, div13);
    			append_dev(div13, p12);
    			append_dev(p12, t45);
    			append_dev(div13, t46);
    			append_dev(div13, p13);
    			append_dev(div17, t48);
    			append_dev(div17, div16);
    			append_dev(div16, div15);
    			append_dev(div24, t49);
    			append_dev(div24, div23);
    			append_dev(div23, div20);
    			append_dev(div20, div18);
    			append_dev(div18, p14);
    			append_dev(p14, t50);
    			append_dev(div18, t51);
    			append_dev(div18, p15);
    			append_dev(div20, t53);
    			append_dev(div20, div19);
    			append_dev(div19, p16);
    			append_dev(p16, t54);
    			append_dev(div19, t55);
    			append_dev(div19, p17);
    			append_dev(div23, t57);
    			append_dev(div23, div22);
    			append_dev(div22, div21);
    			insert_dev(target, t58, anchor);
    			insert_dev(target, div37, anchor);
    			append_dev(div37, div30);
    			append_dev(div30, div27);
    			append_dev(div27, div25);
    			append_dev(div25, p18);
    			append_dev(p18, t59);
    			append_dev(div25, t60);
    			append_dev(div25, p19);
    			append_dev(div27, t62);
    			append_dev(div27, div26);
    			append_dev(div26, p20);
    			append_dev(p20, t63);
    			append_dev(div26, t64);
    			append_dev(div26, p21);
    			append_dev(div30, t66);
    			append_dev(div30, div29);
    			append_dev(div29, div28);
    			append_dev(div37, t67);
    			append_dev(div37, div36);
    			append_dev(div36, div33);
    			append_dev(div33, div31);
    			append_dev(div31, p22);
    			append_dev(p22, t68);
    			append_dev(div31, t69);
    			append_dev(div31, p23);
    			append_dev(div33, t71);
    			append_dev(div33, div32);
    			append_dev(div32, p24);
    			append_dev(p24, t72);
    			append_dev(div32, t73);
    			append_dev(div32, p25);
    			append_dev(div36, t75);
    			append_dev(div36, div35);
    			append_dev(div35, div34);
    			insert_dev(target, t76, anchor);
    			insert_dev(target, div40, anchor);
    			append_dev(div40, div39);
    			append_dev(div39, div38);
    			insert_dev(target, t77, anchor);
    			insert_dev(target, div46, anchor);
    			append_dev(div46, div41);
    			append_dev(div41, p26);
    			append_dev(p26, t78);
    			append_dev(div41, t79);
    			append_dev(div41, p27);
    			append_dev(div46, t81);
    			append_dev(div46, div42);
    			append_dev(div42, p28);
    			append_dev(p28, t82);
    			append_dev(div42, t83);
    			append_dev(div42, p29);
    			append_dev(div46, t85);
    			append_dev(div46, div43);
    			append_dev(div43, p30);
    			append_dev(p30, t86);
    			append_dev(div43, t87);
    			append_dev(div43, p31);
    			append_dev(div46, t89);
    			append_dev(div46, div44);
    			append_dev(div44, p32);
    			append_dev(p32, t90);
    			append_dev(div44, t91);
    			append_dev(div44, p33);
    			append_dev(div46, t93);
    			append_dev(div46, div45);
    			append_dev(div45, p34);
    			append_dev(p34, t94);
    			append_dev(div45, t95);
    			append_dev(div45, p35);
    			insert_dev(target, t97, anchor);
    			insert_dev(target, div49, anchor);
    			append_dev(div49, div48);
    			append_dev(div48, div47);
    			insert_dev(target, t98, anchor);
    			insert_dev(target, div56, anchor);
    			append_dev(div56, div52);
    			append_dev(div52, div51);
    			append_dev(div51, div50);
    			append_dev(div56, t99);
    			append_dev(div56, div55);
    			append_dev(div55, div54);
    			append_dev(div54, div53);
    			insert_dev(target, t100, anchor);
    			insert_dev(target, div58, anchor);
    			append_dev(div58, nav);
    			append_dev(nav, div57);
    			append_dev(div57, button0);
    			append_dev(button0, t101);
    			append_dev(div57, t102);
    			append_dev(div57, button1);
    			append_dev(button1, t103);
    			append_dev(div58, t104);
    			if_block2.m(div58, null);

    			if (!mounted) {
    				dispose = [
    					listen_dev(button0, "click", /*click_handler_3*/ ctx[21], false, false, false),
    					listen_dev(button1, "click", /*click_handler_4*/ ctx[22], false, false, false)
    				];

    				mounted = true;
    			}
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = /*combat*/ ctx[5].battle.replace(/\(|\)/g, "") + "")) set_data_dev(t0, t0_value);

    			if (/*combat*/ ctx[5].health !== null) {
    				if (if_block0) {
    					if_block0.p(ctx, dirty);
    				} else {
    					if_block0 = create_if_block_9(ctx);
    					if_block0.c();
    					if_block0.m(div1, t2);
    				}
    			} else if (if_block0) {
    				if_block0.d(1);
    				if_block0 = null;
    			}

    			if (dirty[0] & /*combat*/ 32 && t4_value !== (t4_value = [...new Set(/*combat*/ ctx[5].targets.map(func_3))].join(", ") + "")) set_data_dev(t4, t4_value);
    			if (dirty[0] & /*moment, combat*/ 33 && t7_value !== (t7_value = /*moment*/ ctx[0](/*combat*/ ctx[5].start, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "")) set_data_dev(t7, t7_value);
    			if (dirty[0] & /*moment, combat*/ 33 && t9_value !== (t9_value = /*moment*/ ctx[0](/*combat*/ ctx[5].end, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "")) set_data_dev(t9, t9_value);
    			if (dirty[0] & /*combat*/ 32 && t11_value !== (t11_value = /*combat*/ ctx[5].player.replace("@", "") + "")) set_data_dev(t11, t11_value);
    			if (dirty[0] & /*combat*/ 32 && t13_value !== (t13_value = /*combat*/ ctx[5].battle.replace(/\(|\)/g, "") + "")) set_data_dev(t13, t13_value);
    			if (dirty[0] & /*moment, combat*/ 33 && t16_value !== (t16_value = /*moment*/ ctx[0](/*combat*/ ctx[5].start, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "")) set_data_dev(t16, t16_value);
    			if (dirty[0] & /*moment, combat*/ 33 && t19_value !== (t19_value = /*moment*/ ctx[0](/*combat*/ ctx[5].end, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "")) set_data_dev(t19, t19_value);
    			if (dirty[0] & /*combat*/ 32 && t22_value !== (t22_value = /*combat*/ ctx[5].duration + "")) set_data_dev(t22, t22_value);
    			if (dirty[0] & /*logs, selectedFile*/ 136) show_if = Array.isArray(/*logs*/ ctx[7][/*selectedFile*/ ctx[3]]);

    			if (show_if) {
    				if (if_block1) {
    					if_block1.p(ctx, dirty);
    				} else {
    					if_block1 = create_if_block_8(ctx);
    					if_block1.c();
    					if_block1.m(div3, null);
    				}
    			} else if (if_block1) {
    				if_block1.d(1);
    				if_block1 = null;
    			}

    			if (dirty[0] & /*combat*/ 32 && t25_value !== (t25_value = /*combat*/ ctx[5].duration + "")) set_data_dev(t25, t25_value);
    			if (dirty[0] & /*combat*/ 32 && t29_value !== (t29_value = /*combat*/ ctx[5].apm.toFixed(2) + "")) set_data_dev(t29, t29_value);
    			if (dirty[0] & /*combat*/ 32 && t33_value !== (t33_value = /*combat*/ ctx[5].hits + "")) set_data_dev(t33, t33_value);

    			if (dirty[0] & /*combat*/ 32 && t37_value !== (t37_value = /*combat*/ ctx[5].gcdMedian.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t37, t37_value);

    			if (dirty[0] & /*combat*/ 32 && t41_value !== (t41_value = /*combat*/ ctx[5].damage.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t41, t41_value);

    			if (dirty[0] & /*combat*/ 32 && t45_value !== (t45_value = /*combat*/ ctx[5].dps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t45, t45_value);

    			if (dirty[0] & /*combat*/ 32 && t50_value !== (t50_value = /*combat*/ ctx[5].heals.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t50, t50_value);

    			if (dirty[0] & /*combat*/ 32 && t54_value !== (t54_value = /*combat*/ ctx[5].hps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t54, t54_value);

    			if (dirty[0] & /*combat*/ 32 && t59_value !== (t59_value = /*combat*/ ctx[5].damageTaken.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t59, t59_value);

    			if (dirty[0] & /*combat*/ 32 && t63_value !== (t63_value = /*combat*/ ctx[5].dtps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t63, t63_value);

    			if (dirty[0] & /*combat*/ 32 && t68_value !== (t68_value = /*combat*/ ctx[5].threat.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t68, t68_value);

    			if (dirty[0] & /*combat*/ 32 && t72_value !== (t72_value = /*combat*/ ctx[5].tps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t72, t72_value);

    			if (dirty[0] & /*combat*/ 32 && t78_value !== (t78_value = /*combat*/ ctx[5].gcds.length + "")) set_data_dev(t78, t78_value);

    			if (dirty[0] & /*combat*/ 32 && t82_value !== (t82_value = /*combat*/ ctx[5].gcdMedian.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t82, t82_value);

    			if (dirty[0] & /*combat*/ 32 && t86_value !== (t86_value = /*combat*/ ctx[5].gcdMean.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t86, t86_value);

    			if (dirty[0] & /*combat*/ 32 && t90_value !== (t90_value = /*combat*/ ctx[5].gcdMin.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t90, t90_value);

    			if (dirty[0] & /*combat*/ 32 && t94_value !== (t94_value = /*combat*/ ctx[5].gcdMax.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t94, t94_value);

    			if (dirty[0] & /*selectedTab*/ 1024 && button0_aria_selected_value !== (button0_aria_selected_value = /*selectedTab*/ ctx[10] === "damage")) {
    				attr_dev(button0, "aria-selected", button0_aria_selected_value);
    			}

    			if (dirty[0] & /*selectedTab*/ 1024 && button1_aria_selected_value !== (button1_aria_selected_value = /*selectedTab*/ ctx[10] === "heal")) {
    				attr_dev(button1, "aria-selected", button1_aria_selected_value);
    			}

    			if (current_block_type === (current_block_type = select_block_type_2(ctx)) && if_block2) {
    				if_block2.p(ctx, dirty);
    			} else {
    				if_block2.d(1);
    				if_block2 = current_block_type(ctx);

    				if (if_block2) {
    					if_block2.c();
    					if_block2.m(div58, null);
    				}
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div6);
    			if (if_block0) if_block0.d();
    			if (if_block1) if_block1.d();
    			if (detaching) detach_dev(t24);
    			if (detaching) detach_dev(div11);
    			if (detaching) detach_dev(t40);
    			if (detaching) detach_dev(div24);
    			if (detaching) detach_dev(t58);
    			if (detaching) detach_dev(div37);
    			if (detaching) detach_dev(t76);
    			if (detaching) detach_dev(div40);
    			if (detaching) detach_dev(t77);
    			if (detaching) detach_dev(div46);
    			if (detaching) detach_dev(t97);
    			if (detaching) detach_dev(div49);
    			if (detaching) detach_dev(t98);
    			if (detaching) detach_dev(div56);
    			if (detaching) detach_dev(t100);
    			if (detaching) detach_dev(div58);
    			if_block2.d();
    			mounted = false;
    			run_all(dispose);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_1.name,
    		type: "if",
    		source: "(1179:5) {#if typeof combat !== 'undefined'}",
    		ctx
    	});

    	return block;
    }

    // (2037:9) {#each files as file}
    function create_each_block_5(ctx) {
    	let option;
    	let t_value = /*interpretCombatFileName*/ ctx[15](/*file*/ ctx[66]) + "";
    	let t;
    	let option_value_value;

    	const block = {
    		c: function create() {
    			option = element("option");
    			t = text(t_value);
    			option.__value = option_value_value = /*file*/ ctx[66];
    			option.value = option.__value;
    			add_location(option, file$2, 2037, 10, 63775);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, option, anchor);
    			append_dev(option, t);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*files*/ 4 && t_value !== (t_value = /*interpretCombatFileName*/ ctx[15](/*file*/ ctx[66]) + "")) set_data_dev(t, t_value);

    			if (dirty[0] & /*files*/ 4 && option_value_value !== (option_value_value = /*file*/ ctx[66])) {
    				prop_dev(option, "__value", option_value_value);
    				option.value = option.__value;
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(option);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block_5.name,
    		type: "each",
    		source: "(2037:9) {#each files as file}",
    		ctx
    	});

    	return block;
    }

    // (1187:9) {#if combat.health !== null}
    function create_if_block_9(ctx) {
    	let p;
    	let t0;
    	let t1_value = /*combat*/ ctx[5].health.toLocaleString() + "";
    	let t1;

    	const block = {
    		c: function create() {
    			p = element("p");
    			t0 = text("Health: ");
    			t1 = text(t1_value);
    			add_location(p, file$2, 1187, 10, 34343);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, p, anchor);
    			append_dev(p, t0);
    			append_dev(p, t1);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t1_value !== (t1_value = /*combat*/ ctx[5].health.toLocaleString() + "")) set_data_dev(t1, t1_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(p);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_9.name,
    		type: "if",
    		source: "(1187:9) {#if combat.health !== null}",
    		ctx
    	});

    	return block;
    }

    // (1212:11) {#if Array.isArray(logs[selectedFile])}
    function create_if_block_8(ctx) {
    	let each_1_anchor;
    	let each_value_4 = /*logs*/ ctx[7][/*selectedFile*/ ctx[3]];
    	validate_each_argument(each_value_4);
    	let each_blocks = [];

    	for (let i = 0; i < each_value_4.length; i += 1) {
    		each_blocks[i] = create_each_block_4(get_each_context_4(ctx, each_value_4, i));
    	}

    	const block = {
    		c: function create() {
    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].c();
    			}

    			each_1_anchor = empty();
    		},
    		m: function mount(target, anchor) {
    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].m(target, anchor);
    			}

    			insert_dev(target, each_1_anchor, anchor);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*logs, selectedFile, combat, loadStats, moment*/ 8361) {
    				each_value_4 = /*logs*/ ctx[7][/*selectedFile*/ ctx[3]];
    				validate_each_argument(each_value_4);
    				let i;

    				for (i = 0; i < each_value_4.length; i += 1) {
    					const child_ctx = get_each_context_4(ctx, each_value_4, i);

    					if (each_blocks[i]) {
    						each_blocks[i].p(child_ctx, dirty);
    					} else {
    						each_blocks[i] = create_each_block_4(child_ctx);
    						each_blocks[i].c();
    						each_blocks[i].m(each_1_anchor.parentNode, each_1_anchor);
    					}
    				}

    				for (; i < each_blocks.length; i += 1) {
    					each_blocks[i].d(1);
    				}

    				each_blocks.length = each_value_4.length;
    			}
    		},
    		d: function destroy(detaching) {
    			destroy_each(each_blocks, detaching);
    			if (detaching) detach_dev(each_1_anchor);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_8.name,
    		type: "if",
    		source: "(1212:11) {#if Array.isArray(logs[selectedFile])}",
    		ctx
    	});

    	return block;
    }

    // (1213:12) {#each logs[selectedFile] as stat}
    function create_each_block_4(ctx) {
    	let button;
    	let svg;
    	let path_1;
    	let t0;
    	let div;
    	let t1_value = /*stat*/ ctx[63].player.replace("@", "") + "";
    	let t1;
    	let t2;
    	let t3_value = /*stat*/ ctx[63].battle.replace(/\(|\)/g, "") + "";
    	let t3;
    	let t4;
    	let br;
    	let t5;
    	let span0;
    	let t6_value = /*moment*/ ctx[0](/*stat*/ ctx[63].start, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "";
    	let t6;
    	let t7;
    	let t8;
    	let span1;
    	let t9_value = /*moment*/ ctx[0](/*stat*/ ctx[63].end, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "";
    	let t9;
    	let t10;
    	let t11;
    	let span2;
    	let t12_value = /*stat*/ ctx[63].duration + "";
    	let t12;
    	let t13;
    	let button_aria_checked_value;
    	let mounted;
    	let dispose;

    	function click_handler_2() {
    		return /*click_handler_2*/ ctx[20](/*stat*/ ctx[63]);
    	}

    	const block = {
    		c: function create() {
    			button = element("button");
    			svg = svg_element("svg");
    			path_1 = svg_element("path");
    			t0 = space();
    			div = element("div");
    			t1 = text(t1_value);
    			t2 = text(" (");
    			t3 = text(t3_value);
    			t4 = text(")\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
    			br = element("br");
    			t5 = space();
    			span0 = element("span");
    			t6 = text(t6_value);
    			t7 = text(" -");
    			t8 = space();
    			span1 = element("span");
    			t9 = text(t9_value);
    			t10 = text(" -");
    			t11 = space();
    			span2 = element("span");
    			t12 = text(t12_value);
    			t13 = space();
    			attr_dev(path_1, "fill-rule", "evenodd");
    			attr_dev(path_1, "clip-rule", "evenodd");
    			attr_dev(path_1, "d", "M13.78 4.22C13.9204 4.36062 13.9993 4.55125 13.9993 4.75C13.9993 4.94875 13.9204 5.13937 13.78 5.28L6.53 12.53C6.38937 12.6704 6.19875 12.7493 6 12.7493C5.80125 12.7493 5.61062 12.6704 5.47 12.53L2.22 9.28C2.08752 9.13782 2.0154 8.94978 2.01882 8.75547C2.02225 8.56117 2.10096 8.37579 2.23838 8.23837C2.37579 8.10096 2.56118 8.02225 2.75548 8.01882C2.94978 8.01539 3.13782 8.08752 3.28 8.22L6 10.94L12.72 4.22C12.8606 4.07955 13.0512 4.00066 13.25 4.00066C13.4487 4.00066 13.6394 4.07955 13.78 4.22Z");
    			add_location(path_1, file$2, 1224, 15, 35921);
    			attr_dev(svg, "class", "SelectMenu-icon SelectMenu-icon--check octicon octicon-check");
    			attr_dev(svg, "xmlns", "http://www.w3.org/2000/svg");
    			attr_dev(svg, "viewBox", "0 0 16 16");
    			attr_dev(svg, "width", "16");
    			attr_dev(svg, "height", "16");
    			add_location(svg, file$2, 1218, 14, 35678);
    			add_location(br, file$2, 1230, 15, 36647);
    			attr_dev(span0, "class", "f6");
    			add_location(span0, file$2, 1231, 15, 36669);
    			attr_dev(span1, "class", "f6");
    			add_location(span1, file$2, 1232, 15, 36770);
    			attr_dev(span2, "class", "f6");
    			add_location(span2, file$2, 1233, 15, 36869);
    			add_location(div, file$2, 1228, 14, 36542);
    			attr_dev(button, "class", "SelectMenu-item");
    			attr_dev(button, "role", "menuitemcheckbox");
    			attr_dev(button, "aria-checked", button_aria_checked_value = /*stat*/ ctx[63].id === /*combat*/ ctx[5].id);
    			add_location(button, file$2, 1213, 13, 35469);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, button, anchor);
    			append_dev(button, svg);
    			append_dev(svg, path_1);
    			append_dev(button, t0);
    			append_dev(button, div);
    			append_dev(div, t1);
    			append_dev(div, t2);
    			append_dev(div, t3);
    			append_dev(div, t4);
    			append_dev(div, br);
    			append_dev(div, t5);
    			append_dev(div, span0);
    			append_dev(span0, t6);
    			append_dev(span0, t7);
    			append_dev(div, t8);
    			append_dev(div, span1);
    			append_dev(span1, t9);
    			append_dev(span1, t10);
    			append_dev(div, t11);
    			append_dev(div, span2);
    			append_dev(span2, t12);
    			append_dev(button, t13);

    			if (!mounted) {
    				dispose = listen_dev(button, "click", click_handler_2, false, false, false);
    				mounted = true;
    			}
    		},
    		p: function update(new_ctx, dirty) {
    			ctx = new_ctx;
    			if (dirty[0] & /*logs, selectedFile*/ 136 && t1_value !== (t1_value = /*stat*/ ctx[63].player.replace("@", "") + "")) set_data_dev(t1, t1_value);
    			if (dirty[0] & /*logs, selectedFile*/ 136 && t3_value !== (t3_value = /*stat*/ ctx[63].battle.replace(/\(|\)/g, "") + "")) set_data_dev(t3, t3_value);
    			if (dirty[0] & /*moment, logs, selectedFile*/ 137 && t6_value !== (t6_value = /*moment*/ ctx[0](/*stat*/ ctx[63].start, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "")) set_data_dev(t6, t6_value);
    			if (dirty[0] & /*moment, logs, selectedFile*/ 137 && t9_value !== (t9_value = /*moment*/ ctx[0](/*stat*/ ctx[63].end, MOMENT_FORMAT).format("HH:mm:ss.SSS") + "")) set_data_dev(t9, t9_value);
    			if (dirty[0] & /*logs, selectedFile*/ 136 && t12_value !== (t12_value = /*stat*/ ctx[63].duration + "")) set_data_dev(t12, t12_value);

    			if (dirty[0] & /*logs, selectedFile, combat, files*/ 172 && button_aria_checked_value !== (button_aria_checked_value = /*stat*/ ctx[63].id === /*combat*/ ctx[5].id)) {
    				attr_dev(button, "aria-checked", button_aria_checked_value);
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(button);
    			mounted = false;
    			dispose();
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block_4.name,
    		type: "each",
    		source: "(1213:12) {#each logs[selectedFile] as stat}",
    		ctx
    	});

    	return block;
    }

    // (1771:7) {:else}
    function create_else_block(ctx) {
    	let div2;
    	let div0;
    	let h50;
    	let t1;
    	let ul0;
    	let li0;
    	let t3;
    	let li1;
    	let t5;
    	let li2;
    	let t7;
    	let li3;
    	let t9;
    	let li4;
    	let t11;
    	let li5;
    	let t13;
    	let t14;
    	let ul1;
    	let li6;
    	let t16;
    	let li7;
    	let t17_value = /*combat*/ ctx[5].abilityTrigger.filter(func_55).reduce(func_56, 0) + "";
    	let t17;
    	let t18;
    	let li8;

    	let t19_value = /*combat*/ ctx[5].abilityTrigger.filter(func_57).reduce(func_58, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t19;
    	let t20;
    	let li9;

    	let t21_value = /*combat*/ ctx[5].abilityTrigger.filter(func_59).reduce(func_60, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t21;
    	let t22;
    	let li10;
    	let t23_value = /*combat*/ ctx[5].abilityTrigger.filter(func_61).reduce(func_62, 0).toFixed(2) + "";
    	let t23;
    	let t24;
    	let t25;
    	let li11;
    	let show_if_2;
    	let t26;
    	let div1;
    	let h51;
    	let t28;
    	let t29;
    	let ul2;
    	let li12;
    	let t31;
    	let li13;
    	let t32_value = /*combat*/ ctx[5].procsAndTicks.filter(func_68).reduce(func_69, 0) + "";
    	let t32;
    	let t33;
    	let li14;

    	let t34_value = /*combat*/ ctx[5].procsAndTicks.filter(func_70).reduce(func_71, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t34;
    	let t35;
    	let li15;

    	let t36_value = /*combat*/ ctx[5].procsAndTicks.filter(func_72).reduce(func_73, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t36;
    	let t37;
    	let li16;
    	let t38_value = /*combat*/ ctx[5].procsAndTicks.filter(func_74).reduce(func_75, 0).toFixed(2) + "";
    	let t38;
    	let t39;
    	let t40;
    	let li17;
    	let show_if_1;
    	let t41;
    	let ul3;
    	let li18;
    	let t43;
    	let li19;
    	let t44_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_80).reduce(func_81, 0) + "";
    	let t44;
    	let t45;
    	let li20;

    	let t46_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_82).reduce(func_83, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t46;
    	let t47;
    	let li21;

    	let t48_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_84).reduce(func_85, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t48;
    	let t49;
    	let li22;
    	let t50_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_86).reduce(func_87, 0).toFixed(2) + "";
    	let t50;
    	let t51;
    	let t52;
    	let li23;
    	let show_if;
    	let each_value_3 = /*combat*/ ctx[5].abilityTrigger.filter(func_54);
    	validate_each_argument(each_value_3);
    	let each_blocks_1 = [];

    	for (let i = 0; i < each_value_3.length; i += 1) {
    		each_blocks_1[i] = create_each_block_3(get_each_context_3(ctx, each_value_3, i));
    	}

    	function select_block_type_3(ctx, dirty) {
    		if (show_if_2 == null || dirty[0] & /*combat*/ 32) show_if_2 = !!(/*combat*/ ctx[5].abilityTrigger.filter(func_2).length <= 0);
    		if (show_if_2) return create_if_block_6;
    		return create_else_block_3;
    	}

    	let current_block_type = select_block_type_3(ctx, [-1]);
    	let if_block0 = current_block_type(ctx);
    	let each_value_2 = /*combat*/ ctx[5].procsAndTicks.filter(func_67);
    	validate_each_argument(each_value_2);
    	let each_blocks = [];

    	for (let i = 0; i < each_value_2.length; i += 1) {
    		each_blocks[i] = create_each_block_2(get_each_context_2(ctx, each_value_2, i));
    	}

    	function select_block_type_4(ctx, dirty) {
    		if (show_if_1 == null || dirty[0] & /*combat*/ 32) show_if_1 = !!(/*combat*/ ctx[5].procsAndTicks.filter(func_1).length <= 0);
    		if (show_if_1) return create_if_block_5;
    		return create_else_block_2;
    	}

    	let current_block_type_1 = select_block_type_4(ctx, [-1]);
    	let if_block1 = current_block_type_1(ctx);

    	function select_block_type_5(ctx, dirty) {
    		if (show_if == null || dirty[0] & /*combat*/ 32) show_if = !!(/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func).length <= 0);
    		if (show_if) return create_if_block_4;
    		return create_else_block_1;
    	}

    	let current_block_type_2 = select_block_type_5(ctx, [-1]);
    	let if_block2 = current_block_type_2(ctx);

    	const block = {
    		c: function create() {
    			div2 = element("div");
    			div0 = element("div");
    			h50 = element("h5");
    			h50.textContent = "Trigger abilities";
    			t1 = space();
    			ul0 = element("ul");
    			li0 = element("li");
    			li0.textContent = "Ability";
    			t3 = space();
    			li1 = element("li");
    			li1.textContent = "Hits";
    			t5 = space();
    			li2 = element("li");
    			li2.textContent = "Heal";
    			t7 = space();
    			li3 = element("li");
    			li3.textContent = "HPS";
    			t9 = space();
    			li4 = element("li");
    			li4.textContent = "Heal %";
    			t11 = space();
    			li5 = element("li");
    			li5.textContent = "Crit. Hits %";
    			t13 = space();

    			for (let i = 0; i < each_blocks_1.length; i += 1) {
    				each_blocks_1[i].c();
    			}

    			t14 = space();
    			ul1 = element("ul");
    			li6 = element("li");
    			li6.textContent = "Sub-Total";
    			t16 = space();
    			li7 = element("li");
    			t17 = text(t17_value);
    			t18 = space();
    			li8 = element("li");
    			t19 = text(t19_value);
    			t20 = space();
    			li9 = element("li");
    			t21 = text(t21_value);
    			t22 = space();
    			li10 = element("li");
    			t23 = text(t23_value);
    			t24 = text("%");
    			t25 = space();
    			li11 = element("li");
    			if_block0.c();
    			t26 = space();
    			div1 = element("div");
    			h51 = element("h5");
    			h51.textContent = "Procs and Ticks";
    			t28 = space();

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].c();
    			}

    			t29 = space();
    			ul2 = element("ul");
    			li12 = element("li");
    			li12.textContent = "Sub-Total";
    			t31 = space();
    			li13 = element("li");
    			t32 = text(t32_value);
    			t33 = space();
    			li14 = element("li");
    			t34 = text(t34_value);
    			t35 = space();
    			li15 = element("li");
    			t36 = text(t36_value);
    			t37 = space();
    			li16 = element("li");
    			t38 = text(t38_value);
    			t39 = text("%");
    			t40 = space();
    			li17 = element("li");
    			if_block1.c();
    			t41 = space();
    			ul3 = element("ul");
    			li18 = element("li");
    			li18.textContent = "Total";
    			t43 = space();
    			li19 = element("li");
    			t44 = text(t44_value);
    			t45 = space();
    			li20 = element("li");
    			t46 = text(t46_value);
    			t47 = space();
    			li21 = element("li");
    			t48 = text(t48_value);
    			t49 = space();
    			li22 = element("li");
    			t50 = text(t50_value);
    			t51 = text("%");
    			t52 = space();
    			li23 = element("li");
    			if_block2.c();
    			add_location(h50, file$2, 1773, 10, 54791);
    			attr_dev(div0, "class", "Box-row rounded-0");
    			set_style(div0, "background", "#21262d", 1);
    			add_location(div0, file$2, 1772, 9, 54709);
    			attr_dev(li0, "class", "col-3 text-bold");
    			add_location(li0, file$2, 1777, 10, 54922);
    			attr_dev(li1, "class", "col-1 text-bold");
    			add_location(li1, file$2, 1779, 10, 54974);
    			attr_dev(li2, "class", "col-1 text-bold");
    			add_location(li2, file$2, 1781, 10, 55023);
    			attr_dev(li3, "class", "col-1 text-bold");
    			add_location(li3, file$2, 1782, 10, 55071);
    			attr_dev(li4, "class", "col-1 text-bold");
    			add_location(li4, file$2, 1784, 10, 55119);
    			attr_dev(li5, "class", "col-1 text-bold");
    			add_location(li5, file$2, 1785, 10, 55169);
    			attr_dev(ul0, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			add_location(ul0, file$2, 1776, 9, 54844);
    			attr_dev(li6, "class", "h4 col-3 text-bold");
    			add_location(li6, file$2, 1824, 10, 56444);
    			attr_dev(li7, "class", "col-1 text-bold");
    			add_location(li7, file$2, 1826, 10, 56501);
    			attr_dev(li8, "class", "col-1 text-bold");
    			add_location(li8, file$2, 1830, 10, 56666);
    			attr_dev(li9, "class", "col-1 text-bold");
    			add_location(li9, file$2, 1839, 10, 56991);
    			attr_dev(li10, "class", "col-1 text-bold");
    			add_location(li10, file$2, 1849, 10, 57315);
    			attr_dev(li11, "class", "col-1 text-bold");
    			add_location(li11, file$2, 1855, 10, 57540);
    			attr_dev(ul1, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			set_style(ul1, "background", "#b4f1b4", 1);
    			set_style(ul1, "color", "#22272e", 1);
    			add_location(ul1, file$2, 1821, 9, 56279);
    			add_location(h51, file$2, 1873, 10, 58187);
    			attr_dev(div1, "class", "Box-row rounded-0");
    			set_style(div1, "background", "#21262d", 1);
    			add_location(div1, file$2, 1872, 9, 58105);
    			attr_dev(li12, "class", "h4 col-3 text-bold");
    			add_location(li12, file$2, 1907, 10, 59313);
    			attr_dev(li13, "class", "col-1 text-bold");
    			add_location(li13, file$2, 1909, 10, 59370);
    			attr_dev(li14, "class", "col-1 text-bold");
    			add_location(li14, file$2, 1913, 10, 59534);
    			attr_dev(li15, "class", "col-1 text-bold");
    			add_location(li15, file$2, 1922, 10, 59858);
    			attr_dev(li16, "class", "col-1 text-bold");
    			add_location(li16, file$2, 1932, 10, 60181);
    			attr_dev(li17, "class", "col-1 text-bold");
    			add_location(li17, file$2, 1938, 10, 60405);
    			attr_dev(ul2, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			set_style(ul2, "background", "#b4f1b4", 1);
    			set_style(ul2, "color", "#22272e", 1);
    			add_location(ul2, file$2, 1904, 9, 59148);
    			attr_dev(li18, "class", "h4 col-3 text-bold");
    			add_location(li18, file$2, 1958, 10, 61138);
    			attr_dev(li19, "class", "col-1 text-bold");
    			add_location(li19, file$2, 1960, 10, 61191);
    			attr_dev(li20, "class", "col-1 text-bold");
    			add_location(li20, file$2, 1967, 10, 61424);
    			attr_dev(li21, "class", "col-1 text-bold");
    			add_location(li21, file$2, 1977, 10, 61791);
    			attr_dev(li22, "class", "col-1 text-bold");
    			add_location(li22, file$2, 1988, 10, 62157);
    			attr_dev(li23, "class", "col-1 text-bold");
    			add_location(li23, file$2, 1995, 10, 62424);
    			attr_dev(ul3, "class", "Box-row d-flex flex-row flex-justify-between rounded-bottom-3");
    			set_style(ul3, "background", "#245829", 1);
    			set_style(ul3, "color", "#cdd9e5", 1);
    			add_location(ul3, file$2, 1955, 9, 60967);
    			attr_dev(div2, "class", "Box--condensed");
    			set_style(div2, "background", "#2d333b", 1);
    			add_location(div2, file$2, 1771, 8, 54631);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div2, anchor);
    			append_dev(div2, div0);
    			append_dev(div0, h50);
    			append_dev(div2, t1);
    			append_dev(div2, ul0);
    			append_dev(ul0, li0);
    			append_dev(ul0, t3);
    			append_dev(ul0, li1);
    			append_dev(ul0, t5);
    			append_dev(ul0, li2);
    			append_dev(ul0, t7);
    			append_dev(ul0, li3);
    			append_dev(ul0, t9);
    			append_dev(ul0, li4);
    			append_dev(ul0, t11);
    			append_dev(ul0, li5);
    			append_dev(div2, t13);

    			for (let i = 0; i < each_blocks_1.length; i += 1) {
    				each_blocks_1[i].m(div2, null);
    			}

    			append_dev(div2, t14);
    			append_dev(div2, ul1);
    			append_dev(ul1, li6);
    			append_dev(ul1, t16);
    			append_dev(ul1, li7);
    			append_dev(li7, t17);
    			append_dev(ul1, t18);
    			append_dev(ul1, li8);
    			append_dev(li8, t19);
    			append_dev(ul1, t20);
    			append_dev(ul1, li9);
    			append_dev(li9, t21);
    			append_dev(ul1, t22);
    			append_dev(ul1, li10);
    			append_dev(li10, t23);
    			append_dev(li10, t24);
    			append_dev(ul1, t25);
    			append_dev(ul1, li11);
    			if_block0.m(li11, null);
    			append_dev(div2, t26);
    			append_dev(div2, div1);
    			append_dev(div1, h51);
    			append_dev(div2, t28);

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].m(div2, null);
    			}

    			append_dev(div2, t29);
    			append_dev(div2, ul2);
    			append_dev(ul2, li12);
    			append_dev(ul2, t31);
    			append_dev(ul2, li13);
    			append_dev(li13, t32);
    			append_dev(ul2, t33);
    			append_dev(ul2, li14);
    			append_dev(li14, t34);
    			append_dev(ul2, t35);
    			append_dev(ul2, li15);
    			append_dev(li15, t36);
    			append_dev(ul2, t37);
    			append_dev(ul2, li16);
    			append_dev(li16, t38);
    			append_dev(li16, t39);
    			append_dev(ul2, t40);
    			append_dev(ul2, li17);
    			if_block1.m(li17, null);
    			append_dev(div2, t41);
    			append_dev(div2, ul3);
    			append_dev(ul3, li18);
    			append_dev(ul3, t43);
    			append_dev(ul3, li19);
    			append_dev(li19, t44);
    			append_dev(ul3, t45);
    			append_dev(ul3, li20);
    			append_dev(li20, t46);
    			append_dev(ul3, t47);
    			append_dev(ul3, li21);
    			append_dev(li21, t48);
    			append_dev(ul3, t49);
    			append_dev(ul3, li22);
    			append_dev(li22, t50);
    			append_dev(li22, t51);
    			append_dev(ul3, t52);
    			append_dev(ul3, li23);
    			if_block2.m(li23, null);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32) {
    				each_value_3 = /*combat*/ ctx[5].abilityTrigger.filter(func_54);
    				validate_each_argument(each_value_3);
    				let i;

    				for (i = 0; i < each_value_3.length; i += 1) {
    					const child_ctx = get_each_context_3(ctx, each_value_3, i);

    					if (each_blocks_1[i]) {
    						each_blocks_1[i].p(child_ctx, dirty);
    					} else {
    						each_blocks_1[i] = create_each_block_3(child_ctx);
    						each_blocks_1[i].c();
    						each_blocks_1[i].m(div2, t14);
    					}
    				}

    				for (; i < each_blocks_1.length; i += 1) {
    					each_blocks_1[i].d(1);
    				}

    				each_blocks_1.length = each_value_3.length;
    			}

    			if (dirty[0] & /*combat*/ 32 && t17_value !== (t17_value = /*combat*/ ctx[5].abilityTrigger.filter(func_55).reduce(func_56, 0) + "")) set_data_dev(t17, t17_value);

    			if (dirty[0] & /*combat*/ 32 && t19_value !== (t19_value = /*combat*/ ctx[5].abilityTrigger.filter(func_57).reduce(func_58, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t19, t19_value);

    			if (dirty[0] & /*combat*/ 32 && t21_value !== (t21_value = /*combat*/ ctx[5].abilityTrigger.filter(func_59).reduce(func_60, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t21, t21_value);

    			if (dirty[0] & /*combat*/ 32 && t23_value !== (t23_value = /*combat*/ ctx[5].abilityTrigger.filter(func_61).reduce(func_62, 0).toFixed(2) + "")) set_data_dev(t23, t23_value);

    			if (current_block_type === (current_block_type = select_block_type_3(ctx, dirty)) && if_block0) {
    				if_block0.p(ctx, dirty);
    			} else {
    				if_block0.d(1);
    				if_block0 = current_block_type(ctx);

    				if (if_block0) {
    					if_block0.c();
    					if_block0.m(li11, null);
    				}
    			}

    			if (dirty[0] & /*combat*/ 32) {
    				each_value_2 = /*combat*/ ctx[5].procsAndTicks.filter(func_67);
    				validate_each_argument(each_value_2);
    				let i;

    				for (i = 0; i < each_value_2.length; i += 1) {
    					const child_ctx = get_each_context_2(ctx, each_value_2, i);

    					if (each_blocks[i]) {
    						each_blocks[i].p(child_ctx, dirty);
    					} else {
    						each_blocks[i] = create_each_block_2(child_ctx);
    						each_blocks[i].c();
    						each_blocks[i].m(div2, t29);
    					}
    				}

    				for (; i < each_blocks.length; i += 1) {
    					each_blocks[i].d(1);
    				}

    				each_blocks.length = each_value_2.length;
    			}

    			if (dirty[0] & /*combat*/ 32 && t32_value !== (t32_value = /*combat*/ ctx[5].procsAndTicks.filter(func_68).reduce(func_69, 0) + "")) set_data_dev(t32, t32_value);

    			if (dirty[0] & /*combat*/ 32 && t34_value !== (t34_value = /*combat*/ ctx[5].procsAndTicks.filter(func_70).reduce(func_71, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t34, t34_value);

    			if (dirty[0] & /*combat*/ 32 && t36_value !== (t36_value = /*combat*/ ctx[5].procsAndTicks.filter(func_72).reduce(func_73, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t36, t36_value);

    			if (dirty[0] & /*combat*/ 32 && t38_value !== (t38_value = /*combat*/ ctx[5].procsAndTicks.filter(func_74).reduce(func_75, 0).toFixed(2) + "")) set_data_dev(t38, t38_value);

    			if (current_block_type_1 === (current_block_type_1 = select_block_type_4(ctx, dirty)) && if_block1) {
    				if_block1.p(ctx, dirty);
    			} else {
    				if_block1.d(1);
    				if_block1 = current_block_type_1(ctx);

    				if (if_block1) {
    					if_block1.c();
    					if_block1.m(li17, null);
    				}
    			}

    			if (dirty[0] & /*combat*/ 32 && t44_value !== (t44_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_80).reduce(func_81, 0) + "")) set_data_dev(t44, t44_value);

    			if (dirty[0] & /*combat*/ 32 && t46_value !== (t46_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_82).reduce(func_83, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t46, t46_value);

    			if (dirty[0] & /*combat*/ 32 && t48_value !== (t48_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_84).reduce(func_85, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t48, t48_value);

    			if (dirty[0] & /*combat*/ 32 && t50_value !== (t50_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_86).reduce(func_87, 0).toFixed(2) + "")) set_data_dev(t50, t50_value);

    			if (current_block_type_2 === (current_block_type_2 = select_block_type_5(ctx, dirty)) && if_block2) {
    				if_block2.p(ctx, dirty);
    			} else {
    				if_block2.d(1);
    				if_block2 = current_block_type_2(ctx);

    				if (if_block2) {
    					if_block2.c();
    					if_block2.m(li23, null);
    				}
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div2);
    			destroy_each(each_blocks_1, detaching);
    			if_block0.d();
    			destroy_each(each_blocks, detaching);
    			if_block1.d();
    			if_block2.d();
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_else_block.name,
    		type: "else",
    		source: "(1771:7) {:else}",
    		ctx
    	});

    	return block;
    }

    // (1491:7) {#if selectedTab === 'damage'}
    function create_if_block_2(ctx) {
    	let div2;
    	let div0;
    	let h50;
    	let t1;
    	let ul0;
    	let li0;
    	let t3;
    	let li1;
    	let t5;
    	let li2;
    	let t7;
    	let li3;
    	let t9;
    	let li4;
    	let t11;
    	let li5;
    	let t13;
    	let li6;
    	let t15;
    	let li7;
    	let t17;
    	let t18;
    	let ul1;
    	let li8;
    	let t20;
    	let li9;
    	let t21;
    	let li10;
    	let t22_value = /*combat*/ ctx[5].abilityTrigger.filter(func_5).reduce(func_6, 0) + "";
    	let t22;
    	let t23;
    	let li11;

    	let t24_value = /*combat*/ ctx[5].abilityTrigger.filter(func_7).reduce(func_8, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t24;
    	let t25;
    	let li12;

    	let t26_value = /*combat*/ ctx[5].abilityTrigger.filter(func_9).reduce(func_10, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t26;
    	let t27;
    	let li13;
    	let t28_value = /*combat*/ ctx[5].abilityTrigger.filter(func_11).reduce(func_12, 0).toFixed(2) + "";
    	let t28;
    	let t29;
    	let t30;
    	let li14;
    	let t31_value = (/*combat*/ ctx[5].abilityTrigger.filter(func_13).reduce(func_14, 0) / /*combat*/ ctx[5].abilityTrigger.filter(func_15).reduce(func_16, 0) * 100).toFixed(2) + "";
    	let t31;
    	let t32;
    	let t33;
    	let li15;
    	let t34_value = (/*combat*/ ctx[5].abilityTrigger.filter(func_17).reduce(func_18, 0) / /*combat*/ ctx[5].abilityTrigger.filter(func_19).reduce(func_20, 0) * 100).toFixed(2) + "";
    	let t34;
    	let t35;
    	let t36;
    	let div1;
    	let h51;
    	let t38;
    	let t39;
    	let ul2;
    	let li16;
    	let t41;
    	let li17;
    	let t42;
    	let li18;
    	let t43_value = /*combat*/ ctx[5].procsAndTicks.filter(func_22).reduce(func_23, 0) + "";
    	let t43;
    	let t44;
    	let li19;

    	let t45_value = /*combat*/ ctx[5].procsAndTicks.filter(func_24).reduce(func_25, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t45;
    	let t46;
    	let li20;

    	let t47_value = /*combat*/ ctx[5].procsAndTicks.filter(func_26).reduce(func_27, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t47;
    	let t48;
    	let li21;
    	let t49_value = /*combat*/ ctx[5].procsAndTicks.filter(func_28).reduce(func_29, 0).toFixed(2) + "";
    	let t49;
    	let t50;
    	let t51;
    	let li22;
    	let t52_value = (/*combat*/ ctx[5].procsAndTicks.filter(func_30).reduce(func_31, 0) / /*combat*/ ctx[5].procsAndTicks.filter(func_32).reduce(func_33, 0) * 100).toFixed(2) + "";
    	let t52;
    	let t53;
    	let t54;
    	let li23;
    	let t55_value = (/*combat*/ ctx[5].procsAndTicks.filter(func_34).reduce(func_35, 0) / /*combat*/ ctx[5].procsAndTicks.filter(func_36).reduce(func_37, 0) * 100).toFixed(2) + "";
    	let t55;
    	let t56;
    	let t57;
    	let ul3;
    	let li24;
    	let t59;
    	let li25;
    	let t60;
    	let li26;
    	let t61_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_38).reduce(func_39, 0) + "";
    	let t61;
    	let t62;
    	let li27;

    	let t63_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_40).reduce(func_41, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t63;
    	let t64;
    	let li28;

    	let t65_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_42).reduce(func_43, 0).toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t65;
    	let t66;
    	let li29;
    	let t67_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_44).reduce(func_45, 0).toFixed(2) + "";
    	let t67;
    	let t68;
    	let t69;
    	let li30;
    	let t70_value = (/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_46).reduce(func_47, 0) / /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_48).reduce(func_49, 0) * 100).toFixed(2) + "";
    	let t70;
    	let t71;
    	let t72;
    	let li31;
    	let t73_value = (/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_50).reduce(func_51, 0) / /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_52).reduce(func_53, 0) * 100).toFixed(2) + "";
    	let t73;
    	let t74;
    	let each_value_1 = /*combat*/ ctx[5].abilityTrigger.filter(func_4);
    	validate_each_argument(each_value_1);
    	let each_blocks_1 = [];

    	for (let i = 0; i < each_value_1.length; i += 1) {
    		each_blocks_1[i] = create_each_block_1(get_each_context_1(ctx, each_value_1, i));
    	}

    	let each_value = /*combat*/ ctx[5].procsAndTicks.filter(func_21);
    	validate_each_argument(each_value);
    	let each_blocks = [];

    	for (let i = 0; i < each_value.length; i += 1) {
    		each_blocks[i] = create_each_block(get_each_context(ctx, each_value, i));
    	}

    	const block = {
    		c: function create() {
    			div2 = element("div");
    			div0 = element("div");
    			h50 = element("h5");
    			h50.textContent = "Trigger abilities";
    			t1 = space();
    			ul0 = element("ul");
    			li0 = element("li");
    			li0.textContent = "Ability";
    			t3 = space();
    			li1 = element("li");
    			li1.textContent = "Type";
    			t5 = space();
    			li2 = element("li");
    			li2.textContent = "Hits";
    			t7 = space();
    			li3 = element("li");
    			li3.textContent = "DMG";
    			t9 = space();
    			li4 = element("li");
    			li4.textContent = "DPS";
    			t11 = space();
    			li5 = element("li");
    			li5.textContent = "Damage %";
    			t13 = space();
    			li6 = element("li");
    			li6.textContent = "Crit. Hits %";
    			t15 = space();
    			li7 = element("li");
    			li7.textContent = "Miss Hits %";
    			t17 = space();

    			for (let i = 0; i < each_blocks_1.length; i += 1) {
    				each_blocks_1[i].c();
    			}

    			t18 = space();
    			ul1 = element("ul");
    			li8 = element("li");
    			li8.textContent = "Sub-Total";
    			t20 = space();
    			li9 = element("li");
    			t21 = space();
    			li10 = element("li");
    			t22 = text(t22_value);
    			t23 = space();
    			li11 = element("li");
    			t24 = text(t24_value);
    			t25 = space();
    			li12 = element("li");
    			t26 = text(t26_value);
    			t27 = space();
    			li13 = element("li");
    			t28 = text(t28_value);
    			t29 = text("%");
    			t30 = space();
    			li14 = element("li");
    			t31 = text(t31_value);
    			t32 = text("%");
    			t33 = space();
    			li15 = element("li");
    			t34 = text(t34_value);
    			t35 = text("%");
    			t36 = space();
    			div1 = element("div");
    			h51 = element("h5");
    			h51.textContent = "Procs and Ticks";
    			t38 = space();

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].c();
    			}

    			t39 = space();
    			ul2 = element("ul");
    			li16 = element("li");
    			li16.textContent = "Sub-Total";
    			t41 = space();
    			li17 = element("li");
    			t42 = space();
    			li18 = element("li");
    			t43 = text(t43_value);
    			t44 = space();
    			li19 = element("li");
    			t45 = text(t45_value);
    			t46 = space();
    			li20 = element("li");
    			t47 = text(t47_value);
    			t48 = space();
    			li21 = element("li");
    			t49 = text(t49_value);
    			t50 = text("%");
    			t51 = space();
    			li22 = element("li");
    			t52 = text(t52_value);
    			t53 = text("%");
    			t54 = space();
    			li23 = element("li");
    			t55 = text(t55_value);
    			t56 = text("%");
    			t57 = space();
    			ul3 = element("ul");
    			li24 = element("li");
    			li24.textContent = "Total";
    			t59 = space();
    			li25 = element("li");
    			t60 = space();
    			li26 = element("li");
    			t61 = text(t61_value);
    			t62 = space();
    			li27 = element("li");
    			t63 = text(t63_value);
    			t64 = space();
    			li28 = element("li");
    			t65 = text(t65_value);
    			t66 = space();
    			li29 = element("li");
    			t67 = text(t67_value);
    			t68 = text("%");
    			t69 = space();
    			li30 = element("li");
    			t70 = text(t70_value);
    			t71 = text("%");
    			t72 = space();
    			li31 = element("li");
    			t73 = text(t73_value);
    			t74 = text("%");
    			add_location(h50, file$2, 1493, 10, 44923);
    			attr_dev(div0, "class", "Box-row rounded-0");
    			set_style(div0, "background", "#21262d", 1);
    			add_location(div0, file$2, 1492, 9, 44841);
    			attr_dev(li0, "class", "col-3 text-bold");
    			add_location(li0, file$2, 1497, 10, 45054);
    			attr_dev(li1, "class", "col-1 text-bold");
    			add_location(li1, file$2, 1499, 10, 45106);
    			attr_dev(li2, "class", "col-1 text-bold");
    			add_location(li2, file$2, 1500, 10, 45154);
    			attr_dev(li3, "class", "col-1 text-bold");
    			add_location(li3, file$2, 1502, 10, 45203);
    			attr_dev(li4, "class", "col-1 text-bold");
    			add_location(li4, file$2, 1503, 10, 45250);
    			attr_dev(li5, "class", "col-1 text-bold");
    			add_location(li5, file$2, 1505, 10, 45298);
    			attr_dev(li6, "class", "col-1 text-bold");
    			add_location(li6, file$2, 1506, 10, 45350);
    			attr_dev(li7, "class", "col-1 text-bold");
    			add_location(li7, file$2, 1507, 10, 45406);
    			attr_dev(ul0, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			add_location(ul0, file$2, 1496, 9, 44976);
    			attr_dev(li8, "class", "h4 col-3 text-bold");
    			add_location(li8, file$2, 1550, 10, 46847);
    			attr_dev(li9, "class", "col-1 capitalize");
    			add_location(li9, file$2, 1552, 10, 46904);
    			attr_dev(li10, "class", "col-1 text-bold");
    			add_location(li10, file$2, 1553, 10, 46946);
    			attr_dev(li11, "class", "col-1 text-bold");
    			add_location(li11, file$2, 1557, 10, 47111);
    			attr_dev(li12, "class", "col-1 text-bold");
    			add_location(li12, file$2, 1566, 10, 47437);
    			attr_dev(li13, "class", "col-1 text-bold");
    			add_location(li13, file$2, 1576, 10, 47761);
    			attr_dev(li14, "class", "col-1 text-bold");
    			add_location(li14, file$2, 1582, 10, 47988);
    			attr_dev(li15, "class", "col-1 text-bold");
    			add_location(li15, file$2, 1593, 10, 48388);
    			attr_dev(ul1, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			set_style(ul1, "background", "#ffb8b0", 1);
    			set_style(ul1, "color", "#22272e", 1);
    			add_location(ul1, file$2, 1547, 9, 46682);
    			add_location(h51, file$2, 1607, 10, 48881);
    			attr_dev(div1, "class", "Box-row rounded-0");
    			set_style(div1, "background", "#21262d", 1);
    			add_location(div1, file$2, 1606, 9, 48799);
    			attr_dev(li16, "class", "h4 col-3 text-bold");
    			add_location(li16, file$2, 1645, 10, 50172);
    			attr_dev(li17, "class", "col-1 capitalize");
    			add_location(li17, file$2, 1647, 10, 50229);
    			attr_dev(li18, "class", "col-1 text-bold");
    			add_location(li18, file$2, 1648, 10, 50271);
    			attr_dev(li19, "class", "col-1 text-bold");
    			add_location(li19, file$2, 1652, 10, 50435);
    			attr_dev(li20, "class", "col-1 text-bold");
    			add_location(li20, file$2, 1661, 10, 50760);
    			attr_dev(li21, "class", "col-1 text-bold");
    			add_location(li21, file$2, 1671, 10, 51083);
    			attr_dev(li22, "class", "col-1 text-bold");
    			add_location(li22, file$2, 1677, 10, 51309);
    			attr_dev(li23, "class", "col-1 text-bold");
    			add_location(li23, file$2, 1688, 10, 51707);
    			attr_dev(ul2, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			set_style(ul2, "background", "#ffb8b0", 1);
    			set_style(ul2, "color", "#22272e", 1);
    			add_location(ul2, file$2, 1642, 9, 50007);
    			attr_dev(li24, "class", "h4 col-3 text-bold");
    			add_location(li24, file$2, 1704, 10, 52287);
    			attr_dev(li25, "class", "col-1 capitalize");
    			add_location(li25, file$2, 1706, 10, 52340);
    			attr_dev(li26, "class", "col-1 text-bold");
    			add_location(li26, file$2, 1707, 10, 52382);
    			attr_dev(li27, "class", "col-1 text-bold");
    			add_location(li27, file$2, 1714, 10, 52615);
    			attr_dev(li28, "class", "col-1 text-bold");
    			add_location(li28, file$2, 1724, 10, 52983);
    			attr_dev(li29, "class", "col-1 text-bold");
    			add_location(li29, file$2, 1735, 10, 53349);
    			attr_dev(li30, "class", "col-1 text-bold");
    			add_location(li30, file$2, 1742, 10, 53618);
    			attr_dev(li31, "class", "col-1 text-bold");
    			add_location(li31, file$2, 1755, 10, 54105);
    			attr_dev(ul3, "class", "Box-row d-flex flex-row flex-justify-between rounded-bottom-3");
    			set_style(ul3, "background", "#922323", 1);
    			set_style(ul3, "color", "#cdd9e5", 1);
    			add_location(ul3, file$2, 1701, 9, 52116);
    			attr_dev(div2, "class", "Box--condensed");
    			set_style(div2, "background", "#2d333b", 1);
    			add_location(div2, file$2, 1491, 8, 44763);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div2, anchor);
    			append_dev(div2, div0);
    			append_dev(div0, h50);
    			append_dev(div2, t1);
    			append_dev(div2, ul0);
    			append_dev(ul0, li0);
    			append_dev(ul0, t3);
    			append_dev(ul0, li1);
    			append_dev(ul0, t5);
    			append_dev(ul0, li2);
    			append_dev(ul0, t7);
    			append_dev(ul0, li3);
    			append_dev(ul0, t9);
    			append_dev(ul0, li4);
    			append_dev(ul0, t11);
    			append_dev(ul0, li5);
    			append_dev(ul0, t13);
    			append_dev(ul0, li6);
    			append_dev(ul0, t15);
    			append_dev(ul0, li7);
    			append_dev(div2, t17);

    			for (let i = 0; i < each_blocks_1.length; i += 1) {
    				each_blocks_1[i].m(div2, null);
    			}

    			append_dev(div2, t18);
    			append_dev(div2, ul1);
    			append_dev(ul1, li8);
    			append_dev(ul1, t20);
    			append_dev(ul1, li9);
    			append_dev(ul1, t21);
    			append_dev(ul1, li10);
    			append_dev(li10, t22);
    			append_dev(ul1, t23);
    			append_dev(ul1, li11);
    			append_dev(li11, t24);
    			append_dev(ul1, t25);
    			append_dev(ul1, li12);
    			append_dev(li12, t26);
    			append_dev(ul1, t27);
    			append_dev(ul1, li13);
    			append_dev(li13, t28);
    			append_dev(li13, t29);
    			append_dev(ul1, t30);
    			append_dev(ul1, li14);
    			append_dev(li14, t31);
    			append_dev(li14, t32);
    			append_dev(ul1, t33);
    			append_dev(ul1, li15);
    			append_dev(li15, t34);
    			append_dev(li15, t35);
    			append_dev(div2, t36);
    			append_dev(div2, div1);
    			append_dev(div1, h51);
    			append_dev(div2, t38);

    			for (let i = 0; i < each_blocks.length; i += 1) {
    				each_blocks[i].m(div2, null);
    			}

    			append_dev(div2, t39);
    			append_dev(div2, ul2);
    			append_dev(ul2, li16);
    			append_dev(ul2, t41);
    			append_dev(ul2, li17);
    			append_dev(ul2, t42);
    			append_dev(ul2, li18);
    			append_dev(li18, t43);
    			append_dev(ul2, t44);
    			append_dev(ul2, li19);
    			append_dev(li19, t45);
    			append_dev(ul2, t46);
    			append_dev(ul2, li20);
    			append_dev(li20, t47);
    			append_dev(ul2, t48);
    			append_dev(ul2, li21);
    			append_dev(li21, t49);
    			append_dev(li21, t50);
    			append_dev(ul2, t51);
    			append_dev(ul2, li22);
    			append_dev(li22, t52);
    			append_dev(li22, t53);
    			append_dev(ul2, t54);
    			append_dev(ul2, li23);
    			append_dev(li23, t55);
    			append_dev(li23, t56);
    			append_dev(div2, t57);
    			append_dev(div2, ul3);
    			append_dev(ul3, li24);
    			append_dev(ul3, t59);
    			append_dev(ul3, li25);
    			append_dev(ul3, t60);
    			append_dev(ul3, li26);
    			append_dev(li26, t61);
    			append_dev(ul3, t62);
    			append_dev(ul3, li27);
    			append_dev(li27, t63);
    			append_dev(ul3, t64);
    			append_dev(ul3, li28);
    			append_dev(li28, t65);
    			append_dev(ul3, t66);
    			append_dev(ul3, li29);
    			append_dev(li29, t67);
    			append_dev(li29, t68);
    			append_dev(ul3, t69);
    			append_dev(ul3, li30);
    			append_dev(li30, t70);
    			append_dev(li30, t71);
    			append_dev(ul3, t72);
    			append_dev(ul3, li31);
    			append_dev(li31, t73);
    			append_dev(li31, t74);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32) {
    				each_value_1 = /*combat*/ ctx[5].abilityTrigger.filter(func_4);
    				validate_each_argument(each_value_1);
    				let i;

    				for (i = 0; i < each_value_1.length; i += 1) {
    					const child_ctx = get_each_context_1(ctx, each_value_1, i);

    					if (each_blocks_1[i]) {
    						each_blocks_1[i].p(child_ctx, dirty);
    					} else {
    						each_blocks_1[i] = create_each_block_1(child_ctx);
    						each_blocks_1[i].c();
    						each_blocks_1[i].m(div2, t18);
    					}
    				}

    				for (; i < each_blocks_1.length; i += 1) {
    					each_blocks_1[i].d(1);
    				}

    				each_blocks_1.length = each_value_1.length;
    			}

    			if (dirty[0] & /*combat*/ 32 && t22_value !== (t22_value = /*combat*/ ctx[5].abilityTrigger.filter(func_5).reduce(func_6, 0) + "")) set_data_dev(t22, t22_value);

    			if (dirty[0] & /*combat*/ 32 && t24_value !== (t24_value = /*combat*/ ctx[5].abilityTrigger.filter(func_7).reduce(func_8, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t24, t24_value);

    			if (dirty[0] & /*combat*/ 32 && t26_value !== (t26_value = /*combat*/ ctx[5].abilityTrigger.filter(func_9).reduce(func_10, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t26, t26_value);

    			if (dirty[0] & /*combat*/ 32 && t28_value !== (t28_value = /*combat*/ ctx[5].abilityTrigger.filter(func_11).reduce(func_12, 0).toFixed(2) + "")) set_data_dev(t28, t28_value);
    			if (dirty[0] & /*combat*/ 32 && t31_value !== (t31_value = (/*combat*/ ctx[5].abilityTrigger.filter(func_13).reduce(func_14, 0) / /*combat*/ ctx[5].abilityTrigger.filter(func_15).reduce(func_16, 0) * 100).toFixed(2) + "")) set_data_dev(t31, t31_value);
    			if (dirty[0] & /*combat*/ 32 && t34_value !== (t34_value = (/*combat*/ ctx[5].abilityTrigger.filter(func_17).reduce(func_18, 0) / /*combat*/ ctx[5].abilityTrigger.filter(func_19).reduce(func_20, 0) * 100).toFixed(2) + "")) set_data_dev(t34, t34_value);

    			if (dirty[0] & /*combat*/ 32) {
    				each_value = /*combat*/ ctx[5].procsAndTicks.filter(func_21);
    				validate_each_argument(each_value);
    				let i;

    				for (i = 0; i < each_value.length; i += 1) {
    					const child_ctx = get_each_context(ctx, each_value, i);

    					if (each_blocks[i]) {
    						each_blocks[i].p(child_ctx, dirty);
    					} else {
    						each_blocks[i] = create_each_block(child_ctx);
    						each_blocks[i].c();
    						each_blocks[i].m(div2, t39);
    					}
    				}

    				for (; i < each_blocks.length; i += 1) {
    					each_blocks[i].d(1);
    				}

    				each_blocks.length = each_value.length;
    			}

    			if (dirty[0] & /*combat*/ 32 && t43_value !== (t43_value = /*combat*/ ctx[5].procsAndTicks.filter(func_22).reduce(func_23, 0) + "")) set_data_dev(t43, t43_value);

    			if (dirty[0] & /*combat*/ 32 && t45_value !== (t45_value = /*combat*/ ctx[5].procsAndTicks.filter(func_24).reduce(func_25, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t45, t45_value);

    			if (dirty[0] & /*combat*/ 32 && t47_value !== (t47_value = /*combat*/ ctx[5].procsAndTicks.filter(func_26).reduce(func_27, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t47, t47_value);

    			if (dirty[0] & /*combat*/ 32 && t49_value !== (t49_value = /*combat*/ ctx[5].procsAndTicks.filter(func_28).reduce(func_29, 0).toFixed(2) + "")) set_data_dev(t49, t49_value);
    			if (dirty[0] & /*combat*/ 32 && t52_value !== (t52_value = (/*combat*/ ctx[5].procsAndTicks.filter(func_30).reduce(func_31, 0) / /*combat*/ ctx[5].procsAndTicks.filter(func_32).reduce(func_33, 0) * 100).toFixed(2) + "")) set_data_dev(t52, t52_value);
    			if (dirty[0] & /*combat*/ 32 && t55_value !== (t55_value = (/*combat*/ ctx[5].procsAndTicks.filter(func_34).reduce(func_35, 0) / /*combat*/ ctx[5].procsAndTicks.filter(func_36).reduce(func_37, 0) * 100).toFixed(2) + "")) set_data_dev(t55, t55_value);
    			if (dirty[0] & /*combat*/ 32 && t61_value !== (t61_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_38).reduce(func_39, 0) + "")) set_data_dev(t61, t61_value);

    			if (dirty[0] & /*combat*/ 32 && t63_value !== (t63_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_40).reduce(func_41, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t63, t63_value);

    			if (dirty[0] & /*combat*/ 32 && t65_value !== (t65_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_42).reduce(func_43, 0).toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t65, t65_value);

    			if (dirty[0] & /*combat*/ 32 && t67_value !== (t67_value = /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_44).reduce(func_45, 0).toFixed(2) + "")) set_data_dev(t67, t67_value);
    			if (dirty[0] & /*combat*/ 32 && t70_value !== (t70_value = (/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_46).reduce(func_47, 0) / /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_48).reduce(func_49, 0) * 100).toFixed(2) + "")) set_data_dev(t70, t70_value);
    			if (dirty[0] & /*combat*/ 32 && t73_value !== (t73_value = (/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_50).reduce(func_51, 0) / /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_52).reduce(func_53, 0) * 100).toFixed(2) + "")) set_data_dev(t73, t73_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div2);
    			destroy_each(each_blocks_1, detaching);
    			destroy_each(each_blocks, detaching);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_2.name,
    		type: "if",
    		source: "(1491:7) {#if selectedTab === 'damage'}",
    		ctx
    	});

    	return block;
    }

    // (1793:12) {#if ability.precast}
    function create_if_block_7(ctx) {
    	let span;

    	const block = {
    		c: function create() {
    			span = element("span");
    			span.textContent = "(Precast)";
    			attr_dev(span, "class", "f3-light");
    			add_location(span, file$2, 1793, 13, 55507);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, span, anchor);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(span);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_7.name,
    		type: "if",
    		source: "(1793:12) {#if ability.precast}",
    		ctx
    	});

    	return block;
    }

    // (1789:9) {#each combat.abilityTrigger.filter((e) => e.type === 'unknown') as ability}
    function create_each_block_3(ctx) {
    	let div6;
    	let div0;
    	let t0_value = /*ability*/ ctx[54].ability + "";
    	let t0;
    	let t1;
    	let t2;
    	let div1;
    	let t3_value = /*ability*/ ctx[54].hits + "";
    	let t3;
    	let t4;
    	let div2;

    	let t5_value = /*ability*/ ctx[54].heals.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t5;
    	let t6;
    	let div3;

    	let t7_value = /*ability*/ ctx[54].hps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t7;
    	let t8;
    	let div4;
    	let t9_value = /*ability*/ ctx[54].healPercentage.toFixed(2) + "";
    	let t9;
    	let t10;
    	let t11;
    	let div5;
    	let t12_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "";
    	let t12;
    	let t13;
    	let if_block = /*ability*/ ctx[54].precast && create_if_block_7(ctx);

    	const block = {
    		c: function create() {
    			div6 = element("div");
    			div0 = element("div");
    			t0 = text(t0_value);
    			t1 = space();
    			if (if_block) if_block.c();
    			t2 = space();
    			div1 = element("div");
    			t3 = text(t3_value);
    			t4 = space();
    			div2 = element("div");
    			t5 = text(t5_value);
    			t6 = space();
    			div3 = element("div");
    			t7 = text(t7_value);
    			t8 = space();
    			div4 = element("div");
    			t9 = text(t9_value);
    			t10 = text("%");
    			t11 = space();
    			div5 = element("div");
    			t12 = text(t12_value);
    			t13 = text("%");
    			attr_dev(div0, "class", "h4 col-3");
    			add_location(div0, file$2, 1790, 11, 55407);
    			attr_dev(div1, "class", "col-1");
    			add_location(div1, file$2, 1797, 11, 55595);
    			attr_dev(div2, "class", "col-1");
    			add_location(div2, file$2, 1799, 11, 55647);
    			attr_dev(div3, "class", "col-1");
    			add_location(div3, file$2, 1805, 11, 55845);
    			attr_dev(div4, "class", "col-1");
    			add_location(div4, file$2, 1812, 11, 56042);
    			attr_dev(div5, "class", "col-1");
    			add_location(div5, file$2, 1815, 11, 56140);
    			attr_dev(div6, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			add_location(div6, file$2, 1789, 10, 55327);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div6, anchor);
    			append_dev(div6, div0);
    			append_dev(div0, t0);
    			append_dev(div0, t1);
    			if (if_block) if_block.m(div0, null);
    			append_dev(div6, t2);
    			append_dev(div6, div1);
    			append_dev(div1, t3);
    			append_dev(div6, t4);
    			append_dev(div6, div2);
    			append_dev(div2, t5);
    			append_dev(div6, t6);
    			append_dev(div6, div3);
    			append_dev(div3, t7);
    			append_dev(div6, t8);
    			append_dev(div6, div4);
    			append_dev(div4, t9);
    			append_dev(div4, t10);
    			append_dev(div6, t11);
    			append_dev(div6, div5);
    			append_dev(div5, t12);
    			append_dev(div5, t13);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = /*ability*/ ctx[54].ability + "")) set_data_dev(t0, t0_value);

    			if (/*ability*/ ctx[54].precast) {
    				if (if_block) ; else {
    					if_block = create_if_block_7(ctx);
    					if_block.c();
    					if_block.m(div0, null);
    				}
    			} else if (if_block) {
    				if_block.d(1);
    				if_block = null;
    			}

    			if (dirty[0] & /*combat*/ 32 && t3_value !== (t3_value = /*ability*/ ctx[54].hits + "")) set_data_dev(t3, t3_value);

    			if (dirty[0] & /*combat*/ 32 && t5_value !== (t5_value = /*ability*/ ctx[54].heals.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t5, t5_value);

    			if (dirty[0] & /*combat*/ 32 && t7_value !== (t7_value = /*ability*/ ctx[54].hps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t7, t7_value);

    			if (dirty[0] & /*combat*/ 32 && t9_value !== (t9_value = /*ability*/ ctx[54].healPercentage.toFixed(2) + "")) set_data_dev(t9, t9_value);
    			if (dirty[0] & /*combat*/ 32 && t12_value !== (t12_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "")) set_data_dev(t12, t12_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div6);
    			if (if_block) if_block.d();
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block_3.name,
    		type: "each",
    		source: "(1789:9) {#each combat.abilityTrigger.filter((e) => e.type === 'unknown') as ability}",
    		ctx
    	});

    	return block;
    }

    // (1859:11) {:else}
    function create_else_block_3(ctx) {
    	let t0_value = (/*combat*/ ctx[5].abilityTrigger.filter(func_63).reduce(func_64, 0) / /*combat*/ ctx[5].abilityTrigger.filter(func_65).reduce(func_66, 0) * 100).toFixed(2) + "";
    	let t0;
    	let t1;

    	const block = {
    		c: function create() {
    			t0 = text(t0_value);
    			t1 = text("%");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, t0, anchor);
    			insert_dev(target, t1, anchor);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = (/*combat*/ ctx[5].abilityTrigger.filter(func_63).reduce(func_64, 0) / /*combat*/ ctx[5].abilityTrigger.filter(func_65).reduce(func_66, 0) * 100).toFixed(2) + "")) set_data_dev(t0, t0_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(t0);
    			if (detaching) detach_dev(t1);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_else_block_3.name,
    		type: "else",
    		source: "(1859:11) {:else}",
    		ctx
    	});

    	return block;
    }

    // (1857:11) {#if combat.abilityTrigger.filter((e) => e.type === 'unknown').length <= 0}
    function create_if_block_6(ctx) {
    	let t;

    	const block = {
    		c: function create() {
    			t = text("0.00%");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, t, anchor);
    		},
    		p: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(t);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_6.name,
    		type: "if",
    		source: "(1857:11) {#if combat.abilityTrigger.filter((e) => e.type === 'unknown').length <= 0}",
    		ctx
    	});

    	return block;
    }

    // (1877:9) {#each combat.procsAndTicks.filter((e) => e.type === 'unknown') as ability}
    function create_each_block_2(ctx) {
    	let div6;
    	let div0;
    	let t0_value = /*ability*/ ctx[54].ability + "";
    	let t0;
    	let t1;
    	let div1;
    	let t2_value = /*ability*/ ctx[54].hits + "";
    	let t2;
    	let t3;
    	let div2;

    	let t4_value = /*ability*/ ctx[54].heals.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t4;
    	let t5;
    	let div3;

    	let t6_value = /*ability*/ ctx[54].hps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t6;
    	let t7;
    	let div4;
    	let t8_value = /*ability*/ ctx[54].damagePercentage.toFixed(2) + "";
    	let t8;
    	let t9;
    	let t10;
    	let div5;
    	let t11_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "";
    	let t11;
    	let t12;

    	const block = {
    		c: function create() {
    			div6 = element("div");
    			div0 = element("div");
    			t0 = text(t0_value);
    			t1 = space();
    			div1 = element("div");
    			t2 = text(t2_value);
    			t3 = space();
    			div2 = element("div");
    			t4 = text(t4_value);
    			t5 = space();
    			div3 = element("div");
    			t6 = text(t6_value);
    			t7 = space();
    			div4 = element("div");
    			t8 = text(t8_value);
    			t9 = text("%");
    			t10 = space();
    			div5 = element("div");
    			t11 = text(t11_value);
    			t12 = text("%");
    			attr_dev(div0, "class", "h4 col-3");
    			add_location(div0, file$2, 1878, 11, 58404);
    			attr_dev(div1, "class", "col-1");
    			add_location(div1, file$2, 1880, 11, 58462);
    			attr_dev(div2, "class", "col-1");
    			add_location(div2, file$2, 1882, 11, 58514);
    			attr_dev(div3, "class", "col-1");
    			add_location(div3, file$2, 1888, 11, 58712);
    			attr_dev(div4, "class", "col-1");
    			add_location(div4, file$2, 1895, 11, 58909);
    			attr_dev(div5, "class", "col-1");
    			add_location(div5, file$2, 1898, 11, 59009);
    			attr_dev(div6, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			add_location(div6, file$2, 1877, 10, 58324);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div6, anchor);
    			append_dev(div6, div0);
    			append_dev(div0, t0);
    			append_dev(div6, t1);
    			append_dev(div6, div1);
    			append_dev(div1, t2);
    			append_dev(div6, t3);
    			append_dev(div6, div2);
    			append_dev(div2, t4);
    			append_dev(div6, t5);
    			append_dev(div6, div3);
    			append_dev(div3, t6);
    			append_dev(div6, t7);
    			append_dev(div6, div4);
    			append_dev(div4, t8);
    			append_dev(div4, t9);
    			append_dev(div6, t10);
    			append_dev(div6, div5);
    			append_dev(div5, t11);
    			append_dev(div5, t12);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = /*ability*/ ctx[54].ability + "")) set_data_dev(t0, t0_value);
    			if (dirty[0] & /*combat*/ 32 && t2_value !== (t2_value = /*ability*/ ctx[54].hits + "")) set_data_dev(t2, t2_value);

    			if (dirty[0] & /*combat*/ 32 && t4_value !== (t4_value = /*ability*/ ctx[54].heals.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t4, t4_value);

    			if (dirty[0] & /*combat*/ 32 && t6_value !== (t6_value = /*ability*/ ctx[54].hps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t6, t6_value);

    			if (dirty[0] & /*combat*/ 32 && t8_value !== (t8_value = /*ability*/ ctx[54].damagePercentage.toFixed(2) + "")) set_data_dev(t8, t8_value);
    			if (dirty[0] & /*combat*/ 32 && t11_value !== (t11_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "")) set_data_dev(t11, t11_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div6);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block_2.name,
    		type: "each",
    		source: "(1877:9) {#each combat.procsAndTicks.filter((e) => e.type === 'unknown') as ability}",
    		ctx
    	});

    	return block;
    }

    // (1942:11) {:else}
    function create_else_block_2(ctx) {
    	let t0_value = (/*combat*/ ctx[5].procsAndTicks.filter(func_76).reduce(func_77, 0) / /*combat*/ ctx[5].procsAndTicks.filter(func_78).reduce(func_79, 0) * 100).toFixed(2) + "";
    	let t0;
    	let t1;

    	const block = {
    		c: function create() {
    			t0 = text(t0_value);
    			t1 = text("%");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, t0, anchor);
    			insert_dev(target, t1, anchor);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = (/*combat*/ ctx[5].procsAndTicks.filter(func_76).reduce(func_77, 0) / /*combat*/ ctx[5].procsAndTicks.filter(func_78).reduce(func_79, 0) * 100).toFixed(2) + "")) set_data_dev(t0, t0_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(t0);
    			if (detaching) detach_dev(t1);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_else_block_2.name,
    		type: "else",
    		source: "(1942:11) {:else}",
    		ctx
    	});

    	return block;
    }

    // (1940:11) {#if combat.procsAndTicks.filter((e) => e.type === 'unknown').length <= 0}
    function create_if_block_5(ctx) {
    	let t;

    	const block = {
    		c: function create() {
    			t = text("0.00%");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, t, anchor);
    		},
    		p: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(t);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_5.name,
    		type: "if",
    		source: "(1940:11) {#if combat.procsAndTicks.filter((e) => e.type === 'unknown').length <= 0}",
    		ctx
    	});

    	return block;
    }

    // (2001:11) {:else}
    function create_else_block_1(ctx) {
    	let t0_value = (/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_88).reduce(func_89, 0) / /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_90).reduce(func_91, 0) * 100).toFixed(2) + "";
    	let t0;
    	let t1;

    	const block = {
    		c: function create() {
    			t0 = text(t0_value);
    			t1 = text("%");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, t0, anchor);
    			insert_dev(target, t1, anchor);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = (/*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_88).reduce(func_89, 0) / /*combat*/ ctx[5].procsAndTicks.concat(/*combat*/ ctx[5].abilityTrigger).filter(func_90).reduce(func_91, 0) * 100).toFixed(2) + "")) set_data_dev(t0, t0_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(t0);
    			if (detaching) detach_dev(t1);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_else_block_1.name,
    		type: "else",
    		source: "(2001:11) {:else}",
    		ctx
    	});

    	return block;
    }

    // (1997:11) {#if combat.procsAndTicks             .concat(combat.abilityTrigger)             .filter((e) => e.type === 'unknown').length <= 0}
    function create_if_block_4(ctx) {
    	let t;

    	const block = {
    		c: function create() {
    			t = text("0.00%");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, t, anchor);
    		},
    		p: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(t);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_4.name,
    		type: "if",
    		source: "(1997:11) {#if combat.procsAndTicks             .concat(combat.abilityTrigger)             .filter((e) => e.type === 'unknown').length <= 0}",
    		ctx
    	});

    	return block;
    }

    // (1515:12) {#if ability.precast}
    function create_if_block_3(ctx) {
    	let span;

    	const block = {
    		c: function create() {
    			span = element("span");
    			span.textContent = "(Precast)";
    			attr_dev(span, "class", "f3-light");
    			add_location(span, file$2, 1515, 13, 45743);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, span, anchor);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(span);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_3.name,
    		type: "if",
    		source: "(1515:12) {#if ability.precast}",
    		ctx
    	});

    	return block;
    }

    // (1511:9) {#each combat.abilityTrigger.filter((e) => e.type !== 'unknown') as ability}
    function create_each_block_1(ctx) {
    	let div8;
    	let div0;
    	let t0_value = /*ability*/ ctx[54].ability + "";
    	let t0;
    	let t1;
    	let t2;
    	let div1;
    	let t3_value = /*ability*/ ctx[54].type + "";
    	let t3;
    	let t4;
    	let div2;
    	let t5_value = /*ability*/ ctx[54].hits + "";
    	let t5;
    	let t6;
    	let div3;

    	let t7_value = /*ability*/ ctx[54].damage.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t7;
    	let t8;
    	let div4;

    	let t9_value = /*ability*/ ctx[54].dps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t9;
    	let t10;
    	let div5;
    	let t11_value = /*ability*/ ctx[54].damagePercentage.toFixed(2) + "";
    	let t11;
    	let t12;
    	let t13;
    	let div6;
    	let t14_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "";
    	let t14;
    	let t15;
    	let t16;
    	let div7;
    	let t17_value = /*ability*/ ctx[54].missHitsPercentage.toFixed(2) + "";
    	let t17;
    	let t18;
    	let if_block = /*ability*/ ctx[54].precast && create_if_block_3(ctx);

    	const block = {
    		c: function create() {
    			div8 = element("div");
    			div0 = element("div");
    			t0 = text(t0_value);
    			t1 = space();
    			if (if_block) if_block.c();
    			t2 = space();
    			div1 = element("div");
    			t3 = text(t3_value);
    			t4 = space();
    			div2 = element("div");
    			t5 = text(t5_value);
    			t6 = space();
    			div3 = element("div");
    			t7 = text(t7_value);
    			t8 = space();
    			div4 = element("div");
    			t9 = text(t9_value);
    			t10 = space();
    			div5 = element("div");
    			t11 = text(t11_value);
    			t12 = text("%");
    			t13 = space();
    			div6 = element("div");
    			t14 = text(t14_value);
    			t15 = text("%");
    			t16 = space();
    			div7 = element("div");
    			t17 = text(t17_value);
    			t18 = text("%");
    			attr_dev(div0, "class", "h4 col-3");
    			add_location(div0, file$2, 1512, 11, 45643);
    			attr_dev(div1, "class", "col-1 capitalize");
    			add_location(div1, file$2, 1519, 11, 45831);
    			attr_dev(div2, "class", "col-1");
    			add_location(div2, file$2, 1520, 11, 45893);
    			attr_dev(div3, "class", "col-1");
    			add_location(div3, file$2, 1522, 11, 45945);
    			attr_dev(div4, "class", "col-1");
    			add_location(div4, file$2, 1528, 11, 46144);
    			attr_dev(div5, "class", "col-1");
    			add_location(div5, file$2, 1535, 11, 46341);
    			attr_dev(div6, "class", "col-1");
    			add_location(div6, file$2, 1538, 11, 46441);
    			attr_dev(div7, "class", "col-1");
    			add_location(div7, file$2, 1541, 11, 46547);
    			attr_dev(div8, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			add_location(div8, file$2, 1511, 10, 45563);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div8, anchor);
    			append_dev(div8, div0);
    			append_dev(div0, t0);
    			append_dev(div0, t1);
    			if (if_block) if_block.m(div0, null);
    			append_dev(div8, t2);
    			append_dev(div8, div1);
    			append_dev(div1, t3);
    			append_dev(div8, t4);
    			append_dev(div8, div2);
    			append_dev(div2, t5);
    			append_dev(div8, t6);
    			append_dev(div8, div3);
    			append_dev(div3, t7);
    			append_dev(div8, t8);
    			append_dev(div8, div4);
    			append_dev(div4, t9);
    			append_dev(div8, t10);
    			append_dev(div8, div5);
    			append_dev(div5, t11);
    			append_dev(div5, t12);
    			append_dev(div8, t13);
    			append_dev(div8, div6);
    			append_dev(div6, t14);
    			append_dev(div6, t15);
    			append_dev(div8, t16);
    			append_dev(div8, div7);
    			append_dev(div7, t17);
    			append_dev(div7, t18);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = /*ability*/ ctx[54].ability + "")) set_data_dev(t0, t0_value);

    			if (/*ability*/ ctx[54].precast) {
    				if (if_block) ; else {
    					if_block = create_if_block_3(ctx);
    					if_block.c();
    					if_block.m(div0, null);
    				}
    			} else if (if_block) {
    				if_block.d(1);
    				if_block = null;
    			}

    			if (dirty[0] & /*combat*/ 32 && t3_value !== (t3_value = /*ability*/ ctx[54].type + "")) set_data_dev(t3, t3_value);
    			if (dirty[0] & /*combat*/ 32 && t5_value !== (t5_value = /*ability*/ ctx[54].hits + "")) set_data_dev(t5, t5_value);

    			if (dirty[0] & /*combat*/ 32 && t7_value !== (t7_value = /*ability*/ ctx[54].damage.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t7, t7_value);

    			if (dirty[0] & /*combat*/ 32 && t9_value !== (t9_value = /*ability*/ ctx[54].dps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t9, t9_value);

    			if (dirty[0] & /*combat*/ 32 && t11_value !== (t11_value = /*ability*/ ctx[54].damagePercentage.toFixed(2) + "")) set_data_dev(t11, t11_value);
    			if (dirty[0] & /*combat*/ 32 && t14_value !== (t14_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "")) set_data_dev(t14, t14_value);
    			if (dirty[0] & /*combat*/ 32 && t17_value !== (t17_value = /*ability*/ ctx[54].missHitsPercentage.toFixed(2) + "")) set_data_dev(t17, t17_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div8);
    			if (if_block) if_block.d();
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block_1.name,
    		type: "each",
    		source: "(1511:9) {#each combat.abilityTrigger.filter((e) => e.type !== 'unknown') as ability}",
    		ctx
    	});

    	return block;
    }

    // (1611:9) {#each combat.procsAndTicks.filter((e) => e.type !== 'unknown') as ability}
    function create_each_block(ctx) {
    	let div8;
    	let div0;
    	let t0_value = /*ability*/ ctx[54].ability + "";
    	let t0;
    	let t1;
    	let div1;
    	let t2_value = /*ability*/ ctx[54].type + "";
    	let t2;
    	let t3;
    	let div2;
    	let t4_value = /*ability*/ ctx[54].hits + "";
    	let t4;
    	let t5;
    	let div3;

    	let t6_value = /*ability*/ ctx[54].damage.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t6;
    	let t7;
    	let div4;

    	let t8_value = /*ability*/ ctx[54].dps.toLocaleString(undefined, {
    		minimumFractionDigits: 2,
    		maximumFractionDigits: 2
    	}) + "";

    	let t8;
    	let t9;
    	let div5;
    	let t10_value = /*ability*/ ctx[54].damagePercentage.toFixed(2) + "";
    	let t10;
    	let t11;
    	let t12;
    	let div6;
    	let t13_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "";
    	let t13;
    	let t14;
    	let t15;
    	let div7;
    	let t16_value = /*ability*/ ctx[54].missHitsPercentage.toFixed(2) + "";
    	let t16;
    	let t17;

    	const block = {
    		c: function create() {
    			div8 = element("div");
    			div0 = element("div");
    			t0 = text(t0_value);
    			t1 = space();
    			div1 = element("div");
    			t2 = text(t2_value);
    			t3 = space();
    			div2 = element("div");
    			t4 = text(t4_value);
    			t5 = space();
    			div3 = element("div");
    			t6 = text(t6_value);
    			t7 = space();
    			div4 = element("div");
    			t8 = text(t8_value);
    			t9 = space();
    			div5 = element("div");
    			t10 = text(t10_value);
    			t11 = text("%");
    			t12 = space();
    			div6 = element("div");
    			t13 = text(t13_value);
    			t14 = text("%");
    			t15 = space();
    			div7 = element("div");
    			t16 = text(t16_value);
    			t17 = text("%");
    			attr_dev(div0, "class", "h4 col-3");
    			add_location(div0, file$2, 1612, 11, 49098);
    			attr_dev(div1, "class", "col-1 capitalize");
    			add_location(div1, file$2, 1614, 11, 49156);
    			attr_dev(div2, "class", "col-1");
    			add_location(div2, file$2, 1615, 11, 49218);
    			attr_dev(div3, "class", "col-1");
    			add_location(div3, file$2, 1617, 11, 49270);
    			attr_dev(div4, "class", "col-1");
    			add_location(div4, file$2, 1623, 11, 49469);
    			attr_dev(div5, "class", "col-1");
    			add_location(div5, file$2, 1630, 11, 49666);
    			attr_dev(div6, "class", "col-1");
    			add_location(div6, file$2, 1633, 11, 49766);
    			attr_dev(div7, "class", "col-1");
    			add_location(div7, file$2, 1636, 11, 49872);
    			attr_dev(div8, "class", "Box-row d-flex flex-row flex-justify-between rounded-0");
    			add_location(div8, file$2, 1611, 10, 49018);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div8, anchor);
    			append_dev(div8, div0);
    			append_dev(div0, t0);
    			append_dev(div8, t1);
    			append_dev(div8, div1);
    			append_dev(div1, t2);
    			append_dev(div8, t3);
    			append_dev(div8, div2);
    			append_dev(div2, t4);
    			append_dev(div8, t5);
    			append_dev(div8, div3);
    			append_dev(div3, t6);
    			append_dev(div8, t7);
    			append_dev(div8, div4);
    			append_dev(div4, t8);
    			append_dev(div8, t9);
    			append_dev(div8, div5);
    			append_dev(div5, t10);
    			append_dev(div5, t11);
    			append_dev(div8, t12);
    			append_dev(div8, div6);
    			append_dev(div6, t13);
    			append_dev(div6, t14);
    			append_dev(div8, t15);
    			append_dev(div8, div7);
    			append_dev(div7, t16);
    			append_dev(div7, t17);
    		},
    		p: function update(ctx, dirty) {
    			if (dirty[0] & /*combat*/ 32 && t0_value !== (t0_value = /*ability*/ ctx[54].ability + "")) set_data_dev(t0, t0_value);
    			if (dirty[0] & /*combat*/ 32 && t2_value !== (t2_value = /*ability*/ ctx[54].type + "")) set_data_dev(t2, t2_value);
    			if (dirty[0] & /*combat*/ 32 && t4_value !== (t4_value = /*ability*/ ctx[54].hits + "")) set_data_dev(t4, t4_value);

    			if (dirty[0] & /*combat*/ 32 && t6_value !== (t6_value = /*ability*/ ctx[54].damage.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t6, t6_value);

    			if (dirty[0] & /*combat*/ 32 && t8_value !== (t8_value = /*ability*/ ctx[54].dps.toLocaleString(undefined, {
    				minimumFractionDigits: 2,
    				maximumFractionDigits: 2
    			}) + "")) set_data_dev(t8, t8_value);

    			if (dirty[0] & /*combat*/ 32 && t10_value !== (t10_value = /*ability*/ ctx[54].damagePercentage.toFixed(2) + "")) set_data_dev(t10, t10_value);
    			if (dirty[0] & /*combat*/ 32 && t13_value !== (t13_value = /*ability*/ ctx[54].criticalHitsPercentage.toFixed(2) + "")) set_data_dev(t13, t13_value);
    			if (dirty[0] & /*combat*/ 32 && t16_value !== (t16_value = /*ability*/ ctx[54].missHitsPercentage.toFixed(2) + "")) set_data_dev(t16, t16_value);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div8);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_each_block.name,
    		type: "each",
    		source: "(1611:9) {#each combat.procsAndTicks.filter((e) => e.type !== 'unknown') as ability}",
    		ctx
    	});

    	return block;
    }

    function create_fragment$2(ctx) {
    	let t0;
    	let div7;
    	let div6;
    	let div5;
    	let div0;
    	let a0;
    	let img;
    	let img_src_value;
    	let t1;
    	let span;
    	let t3;
    	let div1;
    	let t4;
    	let div2;
    	let a1;
    	let t6;
    	let div3;
    	let a2;
    	let t8;
    	let div4;
    	let button;
    	let t9;
    	let t10;
    	let current_block_type_index;
    	let if_block1;
    	let if_block1_anchor;
    	let current;
    	let mounted;
    	let dispose;
    	let if_block0 = /*loading*/ ctx[1] && create_if_block_13(ctx);
    	const if_block_creators = [create_if_block, create_if_block_12, create_else_block_4];
    	const if_blocks = [];

    	function select_block_type(ctx, dirty) {
    		if (/*selectedMenu*/ ctx[6] === "analyze") return 0;
    		if (/*selectedMenu*/ ctx[6] === "settings") return 1;
    		return 2;
    	}

    	current_block_type_index = select_block_type(ctx);
    	if_block1 = if_blocks[current_block_type_index] = if_block_creators[current_block_type_index](ctx);

    	const block = {
    		c: function create() {
    			if (if_block0) if_block0.c();
    			t0 = space();
    			div7 = element("div");
    			div6 = element("div");
    			div5 = element("div");
    			div0 = element("div");
    			a0 = element("a");
    			img = element("img");
    			t1 = space();
    			span = element("span");
    			span.textContent = "Trace Parse";
    			t3 = space();
    			div1 = element("div");
    			t4 = space();
    			div2 = element("div");
    			a1 = element("a");
    			a1.textContent = "Analyze Logs";
    			t6 = space();
    			div3 = element("div");
    			a2 = element("a");
    			a2.textContent = "Preferences";
    			t8 = space();
    			div4 = element("div");
    			button = element("button");
    			t9 = text("Parse");
    			t10 = space();
    			if_block1.c();
    			if_block1_anchor = empty();
    			if (img.src !== (img_src_value = "logo.svg")) attr_dev(img, "src", img_src_value);
    			set_style(img, "height", "32px");
    			attr_dev(img, "class", "mr-2");
    			add_location(img, file$2, 1121, 5, 32251);
    			attr_dev(span, "class", "poppins");
    			add_location(span, file$2, 1122, 5, 32314);
    			attr_dev(a0, "href", "#");
    			attr_dev(a0, "class", "Header-link f4 d-flex flex-items-center");
    			add_location(a0, file$2, 1120, 4, 32185);
    			attr_dev(div0, "class", "Header-item");
    			add_location(div0, file$2, 1119, 3, 32155);
    			attr_dev(div1, "class", "Header-item Header-item--full");
    			add_location(div1, file$2, 1126, 3, 32378);
    			attr_dev(a1, "href", "#");
    			attr_dev(a1, "class", "Header-link");
    			add_location(a1, file$2, 1128, 4, 32457);
    			attr_dev(div2, "class", "Header-item");
    			add_location(div2, file$2, 1127, 3, 32427);
    			attr_dev(a2, "href", "#");
    			attr_dev(a2, "class", "Header-link");
    			add_location(a2, file$2, 1134, 4, 32696);
    			attr_dev(div3, "class", "Header-item");
    			add_location(div3, file$2, 1133, 3, 32666);
    			attr_dev(button, "class", "btn btn-outline");
    			attr_dev(button, "type", "button");
    			attr_dev(button, "aria-selected", /*realTimeParse*/ ctx[4]);
    			add_location(button, file$2, 1137, 4, 32835);
    			attr_dev(div4, "class", "Header-item");
    			add_location(div4, file$2, 1136, 3, 32805);
    			attr_dev(div5, "class", "Header color-shadow-medium");
    			add_location(div5, file$2, 1118, 2, 32111);
    			attr_dev(div6, "class", "Layout-main");
    			add_location(div6, file$2, 1117, 1, 32083);
    			attr_dev(div7, "class", "Layout Layout--sidebarPosition-flowRow-start Layout--gutter-none");
    			set_style(div7, "position", "sticky");
    			set_style(div7, "top", "0");
    			set_style(div7, "z-index", "10");
    			add_location(div7, file$2, 1114, 0, 31954);
    		},
    		l: function claim(nodes) {
    			throw new Error("options.hydrate only works if the component was compiled with the `hydratable: true` option");
    		},
    		m: function mount(target, anchor) {
    			if (if_block0) if_block0.m(target, anchor);
    			insert_dev(target, t0, anchor);
    			insert_dev(target, div7, anchor);
    			append_dev(div7, div6);
    			append_dev(div6, div5);
    			append_dev(div5, div0);
    			append_dev(div0, a0);
    			append_dev(a0, img);
    			append_dev(a0, t1);
    			append_dev(a0, span);
    			append_dev(div5, t3);
    			append_dev(div5, div1);
    			append_dev(div5, t4);
    			append_dev(div5, div2);
    			append_dev(div2, a1);
    			append_dev(div5, t6);
    			append_dev(div5, div3);
    			append_dev(div3, a2);
    			append_dev(div5, t8);
    			append_dev(div5, div4);
    			append_dev(div4, button);
    			append_dev(button, t9);
    			insert_dev(target, t10, anchor);
    			if_blocks[current_block_type_index].m(target, anchor);
    			insert_dev(target, if_block1_anchor, anchor);
    			current = true;

    			if (!mounted) {
    				dispose = [
    					listen_dev(a1, "click", /*click_handler*/ ctx[17], false, false, false),
    					listen_dev(a2, "click", /*click_handler_1*/ ctx[18], false, false, false),
    					listen_dev(button, "click", /*toggleParse*/ ctx[14], false, false, false)
    				];

    				mounted = true;
    			}
    		},
    		p: function update(ctx, dirty) {
    			if (/*loading*/ ctx[1]) {
    				if (if_block0) {
    					if_block0.p(ctx, dirty);

    					if (dirty[0] & /*loading*/ 2) {
    						transition_in(if_block0, 1);
    					}
    				} else {
    					if_block0 = create_if_block_13(ctx);
    					if_block0.c();
    					transition_in(if_block0, 1);
    					if_block0.m(t0.parentNode, t0);
    				}
    			} else if (if_block0) {
    				group_outros();

    				transition_out(if_block0, 1, 1, () => {
    					if_block0 = null;
    				});

    				check_outros();
    			}

    			if (!current || dirty[0] & /*realTimeParse*/ 16) {
    				attr_dev(button, "aria-selected", /*realTimeParse*/ ctx[4]);
    			}

    			let previous_block_index = current_block_type_index;
    			current_block_type_index = select_block_type(ctx);

    			if (current_block_type_index === previous_block_index) {
    				if_blocks[current_block_type_index].p(ctx, dirty);
    			} else {
    				group_outros();

    				transition_out(if_blocks[previous_block_index], 1, 1, () => {
    					if_blocks[previous_block_index] = null;
    				});

    				check_outros();
    				if_block1 = if_blocks[current_block_type_index];

    				if (!if_block1) {
    					if_block1 = if_blocks[current_block_type_index] = if_block_creators[current_block_type_index](ctx);
    					if_block1.c();
    				} else {
    					if_block1.p(ctx, dirty);
    				}

    				transition_in(if_block1, 1);
    				if_block1.m(if_block1_anchor.parentNode, if_block1_anchor);
    			}
    		},
    		i: function intro(local) {
    			if (current) return;
    			transition_in(if_block0);
    			transition_in(if_block1);
    			current = true;
    		},
    		o: function outro(local) {
    			transition_out(if_block0);
    			transition_out(if_block1);
    			current = false;
    		},
    		d: function destroy(detaching) {
    			if (if_block0) if_block0.d(detaching);
    			if (detaching) detach_dev(t0);
    			if (detaching) detach_dev(div7);
    			if (detaching) detach_dev(t10);
    			if_blocks[current_block_type_index].d(detaching);
    			if (detaching) detach_dev(if_block1_anchor);
    			mounted = false;
    			run_all(dispose);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_fragment$2.name,
    		type: "component",
    		source: "",
    		ctx
    	});

    	return block;
    }

    const MOMENT_FORMAT = "YYYY-MM-DD HH:mm:ss.SS";
    const func = e => e.type === "unknown";
    const func_1 = e => e.type === "unknown";
    const func_2 = e => e.type === "unknown";
    const func_3 = e => e.substring(0, e.indexOf("{")).trim();
    const func_4 = e => e.type !== "unknown";
    const func_5 = e => e.type !== "unknown";
    const func_6 = (acc, el) => acc + el.hits;
    const func_7 = e => e.type !== "unknown";
    const func_8 = (acc, el) => acc + el.damage;
    const func_9 = e => e.type !== "unknown";
    const func_10 = (acc, el) => acc + el.dps;
    const func_11 = e => e.type !== "unknown";
    const func_12 = (acc, el) => acc + el.damagePercentage;
    const func_13 = e => e.type !== "unknown";
    const func_14 = (acc, el) => acc + el.criticalHits;
    const func_15 = e => e.type !== "unknown";
    const func_16 = (acc, el) => acc + el.hits;
    const func_17 = e => e.type !== "unknown";
    const func_18 = (acc, el) => acc + el.missHits;
    const func_19 = e => e.type !== "unknown";
    const func_20 = (acc, el) => acc + el.hits;
    const func_21 = e => e.type !== "unknown";
    const func_22 = e => e.type !== "unknown";
    const func_23 = (acc, el) => acc + el.hits;
    const func_24 = e => e.type !== "unknown";
    const func_25 = (acc, el) => acc + el.damage;
    const func_26 = e => e.type !== "unknown";
    const func_27 = (acc, el) => acc + el.dps;
    const func_28 = e => e.type !== "unknown";
    const func_29 = (acc, el) => acc + el.damagePercentage;
    const func_30 = e => e.type !== "unknown";
    const func_31 = (acc, el) => acc + el.criticalHits;
    const func_32 = e => e.type !== "unknown";
    const func_33 = (acc, el) => acc + el.hits;
    const func_34 = e => e.type !== "unknown";
    const func_35 = (acc, el) => acc + el.missHits;
    const func_36 = e => e.type !== "unknown";
    const func_37 = (acc, el) => acc + el.hits;
    const func_38 = e => e.type !== "unknown";
    const func_39 = (acc, el) => acc + el.hits;
    const func_40 = e => e.type !== "unknown";
    const func_41 = (acc, el) => acc + el.damage;
    const func_42 = e => e.type !== "unknown";
    const func_43 = (acc, el) => acc + el.dps;
    const func_44 = e => e.type !== "unknown";
    const func_45 = (acc, el) => acc + el.damagePercentage;
    const func_46 = e => e.type !== "unknown";
    const func_47 = (acc, el) => acc + el.criticalHits;
    const func_48 = e => e.type !== "unknown";
    const func_49 = (acc, el) => acc + el.hits;
    const func_50 = e => e.type !== "unknown";
    const func_51 = (acc, el) => acc + el.missHits;
    const func_52 = e => e.type !== "unknown";
    const func_53 = (acc, el) => acc + el.hits;
    const func_54 = e => e.type === "unknown";
    const func_55 = e => e.type === "unknown";
    const func_56 = (acc, el) => acc + el.hits;
    const func_57 = e => e.type === "unknown";
    const func_58 = (acc, el) => acc + el.heals;
    const func_59 = e => e.type === "unknown";
    const func_60 = (acc, el) => acc + el.hps;
    const func_61 = e => e.type === "unknown";
    const func_62 = (acc, el) => acc + el.healPercentage;
    const func_63 = e => e.type === "unknown";
    const func_64 = (acc, el) => acc + el.criticalHits;
    const func_65 = e => e.type === "unknown";
    const func_66 = (acc, el) => acc + el.hits;
    const func_67 = e => e.type === "unknown";
    const func_68 = e => e.type === "unknown";
    const func_69 = (acc, el) => acc + el.hits;
    const func_70 = e => e.type === "unknown";
    const func_71 = (acc, el) => acc + el.heals;
    const func_72 = e => e.type === "unknown";
    const func_73 = (acc, el) => acc + el.hps;
    const func_74 = e => e.type === "unknown";
    const func_75 = (acc, el) => acc + el.healPercentage;
    const func_76 = e => e.type === "unknown";
    const func_77 = (acc, el) => acc + el.criticalHits;
    const func_78 = e => e.type === "unknown";
    const func_79 = (acc, el) => acc + el.hits;
    const func_80 = e => e.type === "unknown";
    const func_81 = (acc, el) => acc + el.hits;
    const func_82 = e => e.type === "unknown";
    const func_83 = (acc, el) => acc + el.heals;
    const func_84 = e => e.type === "unknown";
    const func_85 = (acc, el) => acc + el.hps;
    const func_86 = e => e.type === "unknown";
    const func_87 = (acc, el) => acc + el.healPercentage;
    const func_88 = e => e.type === "unknown";
    const func_89 = (acc, el) => acc + el.criticalHits;
    const func_90 = e => e.type === "unknown";
    const func_91 = (acc, el) => acc + el.hits;

    function instance$2($$self, $$props, $$invalidate) {
    	let { $$slots: slots = {}, $$scope } = $$props;
    	validate_slots("App", slots, []);
    	const moment = require("moment-timezone");
    	const { median } = require("mathjs");
    	const _ = require("lodash");
    	const fs = require("fs");
    	const os = require("os");
    	const fsPromises = fs.promises;
    	const { ipcRenderer } = require("electron");
    	const parse = new Worker("parseFile.js");
    	moment.defaultFormat = "YYYY-MM-DD HH:mm:ss.SS";
    	let loading = false;

    	// let path = 'D:/Work/tech.jaspe.swtor_parse_desktop_app/test';
    	let path = os.userInfo().homedir.replace(/\\/g, "/") + "/Documents/Star Wars - The Old Republic/CombatLogs";

    	let files = [];
    	let selectedFile = "";
    	let selectedFilePrevious = "";
    	let realTimeParse = false;
    	let loadDPSTimeLineReload;
    	let loadHPSTimeLineReload;
    	let loadDTPSTimeLineReload;
    	let loadTPSTimeLineReload;
    	let loadHitsGraphicReload;
    	let loadGCDPerAbilityReload;
    	let loadGCD_DPSTimelineReload;
    	let loadGCD_HPSTimelineReload;
    	let combat;
    	let currentCombatHasChanges = null;
    	let selectedMenu;
    	let logs = {};
    	let percentage = 0;
    	let noFileSelected = false;
    	let selectedTab = "damage";

    	async function onSelectedMenuItem(item) {
    		$$invalidate(6, selectedMenu = item);
    		await tick();

    		if (selectedMenu === "analyze") {
    			await loadStats(combat);
    		}
    	}

    	function resetDashboard() {
    		ipcRenderer.send("update_personal_stats", {
    			apm: 0,
    			gcd: 1.5,
    			dps: 0,
    			hps: 0,
    			dtps: 0,
    			htps: 0,
    			dabps: 0,
    			tps: 0,
    			delay: 0
    		});

    		$$invalidate(10, selectedTab = "damage");
    		$$invalidate(5, combat = undefined);
    		disposeCharts();
    	}

    	async function selectCombat() {
    		await tick();
    		if (realTimeParse) return;
    		if (selectedFile === "") return;
    		if (selectedFilePrevious === selectedFile) return;
    		$$invalidate(9, noFileSelected = true);
    		$$invalidate(10, selectedTab = "damage");
    		disposeCharts();
    		$$invalidate(5, combat = undefined);
    		await tick();
    		selectedFilePrevious = selectedFile;
    		$$invalidate(8, percentage = 0);
    		$$invalidate(1, loading = true);

    		if (logs[selectedFile] === null && !Array.isArray(logs[selectedFile])) {
    			parse.postMessage(JSON.stringify({
    				type: "parse_logs",
    				path,
    				files: [selectedFile]
    			}));
    		} else {
    			await loadStats(logs[selectedFile][0]);
    			$$invalidate(1, loading = false);
    		}
    	}

    	async function loadStats(c) {
    		if (typeof c === "undefined") return;
    		$$invalidate(10, selectedTab = "damage");
    		$$invalidate(5, combat = c);
    		await tick();

    		ipcRenderer.send("update_personal_stats", {
    			apm: combat.apm,
    			gcd: combat.gcdMedian,
    			dps: combat.dps,
    			hps: combat.hps,
    			dtps: combat.dtps,
    			htps: combat.htps,
    			dabps: combat.dabps,
    			tps: combat.tps,
    			delay: moment().diff(moment(combat.logs[combat.logs.length - 1].timestamp, MOMENT_FORMAT), "seconds", true)
    		});

    		if (selectedMenu !== "analyze") return;
    		if (realTimeParse) return;

    		loadDPSTimeLineReload = typeof loadDPSTimeLineReload === "undefined"
    		? loadDPSTimeLine()
    		: loadDPSTimeLineReload();

    		loadHPSTimeLineReload = typeof loadHPSTimeLineReload === "undefined"
    		? loadHPSTimeLine()
    		: loadHPSTimeLineReload();

    		loadDTPSTimeLineReload = typeof loadDTPSTimeLineReload === "undefined"
    		? loadDTPSTimeLine()
    		: loadDTPSTimeLineReload();

    		loadTPSTimeLineReload = typeof loadTPSTimeLineReload === "undefined"
    		? loadTPSTimeLine()
    		: loadTPSTimeLineReload();

    		loadHitsGraphicReload = typeof loadHitsGraphicReload === "undefined"
    		? loadHitsGraphic()
    		: loadHitsGraphicReload();

    		loadGCDPerAbilityReload = typeof loadGCDPerAbilityReload === "undefined"
    		? loadGCDPerAbility()
    		: loadGCDPerAbilityReload();

    		loadGCD_DPSTimelineReload = typeof loadGCD_DPSTimelineReload === "undefined"
    		? loadGCD_DPSTimeline()
    		: loadGCD_DPSTimelineReload();

    		loadGCD_HPSTimelineReload = typeof loadGCD_HPSTimelineReload === "undefined"
    		? loadGCD_HPSTimeline()
    		: loadGCD_HPSTimelineReload();
    	}

    	function disposeCharts() {
    		if (typeof loadDPSTimeLineReload !== "undefined") {
    			loadDPSTimeLineReload(true);
    			loadDPSTimeLineReload = undefined;
    		}

    		if (typeof loadHPSTimeLineReload !== "undefined") {
    			loadHPSTimeLineReload(true);
    			loadHPSTimeLineReload = undefined;
    		}

    		if (typeof loadDTPSTimeLineReload !== "undefined") {
    			loadDTPSTimeLineReload(true);
    			loadDTPSTimeLineReload = undefined;
    		}

    		if (typeof loadTPSTimeLineReload !== "undefined") {
    			loadTPSTimeLineReload(true);
    			loadTPSTimeLineReload = undefined;
    		}

    		if (typeof loadHitsGraphicReload !== "undefined") {
    			loadHitsGraphicReload(true);
    			loadHitsGraphicReload = undefined;
    		}

    		if (typeof loadGCDPerAbilityReload !== "undefined") {
    			loadGCDPerAbilityReload(true);
    			loadGCDPerAbilityReload = undefined;
    		}

    		if (typeof loadGCD_DPSTimelineReload !== "undefined") {
    			loadGCD_DPSTimelineReload(true);
    			loadGCD_DPSTimelineReload = undefined;
    		}

    		if (typeof loadGCD_HPSTimelineReload !== "undefined") {
    			loadGCD_HPSTimelineReload(true);
    			loadGCD_HPSTimelineReload = undefined;
    		}
    	}

    	function loadDPSTimeLine() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const data = combat.abilitiesApply.filter(e => e.damage !== null && e.precast === false).reduce(
    				(acc, el) => [
    					...acc,
    					{
    						ability: el.ability,
    						timestamp: moment(el.timestamp, MOMENT_FORMAT),
    						rawDamage: el.damage.value,
    						damage: acc.length <= 0
    						? el.damage.value
    						: acc[acc.length - 1].damage + el.damage.value
    					}
    				],
    				[]
    			);

    			const dataChart = data.map((el, i) => ({
    				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true),
    				damage: el.rawDamage.toFixed(2),
    				dps: i <= 0
    				? 0
    				: (el.damage / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true)).toFixed(2),
    				additional: el.ability.substring(0, el.ability.indexOf("{"))
    			}));

    			chart.data = dataChart;
    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			// Create chart instance
    			chart = am4core.create("chart-timeline", am4charts.XYChart);

    			chart.scrollbarX = new am4core.Scrollbar();
    			chart.scrollbarY = new am4core.Scrollbar();

    			/* Create axes */
    			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());

    			categoryAxis.dataFields.category = "duration";

    			// categoryAxis.renderer.minGridDistance = 30;
    			/* Create value axis */
    			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

    			valueAxis.min = 0;

    			/* Create series */
    			const columnSeries = chart.series.push(new am4charts.ColumnSeries());

    			columnSeries.name = "Damage";
    			columnSeries.dataFields.valueY = "damage";
    			columnSeries.dataFields.categoryX = "duration";
    			columnSeries.columns.template.tooltipText = "[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]";
    			columnSeries.columns.template.propertyFields.fillOpacity = "fillOpacity";
    			const lineSeries = chart.series.push(new am4charts.LineSeries());
    			lineSeries.name = "DPS";
    			lineSeries.dataFields.valueY = "dps";
    			lineSeries.dataFields.categoryX = "duration";
    			lineSeries.stroke = am4core.color("#c93c37");
    			lineSeries.strokeWidth = 3;
    			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
    			bullet.fill = am4core.color("#c93c37");
    			bullet.tooltipText = "[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]";
    			const circle = bullet.createChild(am4core.Circle);
    			circle.radius = 3;
    			circle.fill = am4core.color("#fff");
    			circle.strokeWidth = 4;
    			chart.legend = new am4charts.Legend();
    			loadDPSTimeLineReload = updateData();
    		});

    		return updateData;
    	}

    	function loadHPSTimeLine() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const data = combat.abilitiesApply.filter(e => e.heal !== null && e.precast === false).reduce(
    				(acc, el) => [
    					...acc,
    					{
    						ability: el.ability,
    						timestamp: moment(el.timestamp, MOMENT_FORMAT),
    						rawHeal: el.heal.value,
    						heal: acc.length <= 0
    						? el.heal.value
    						: acc[acc.length - 1].heal + el.heal.value
    					}
    				],
    				[]
    			);

    			const dataChart = data.map((el, i) => ({
    				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true),
    				heal: el.rawHeal.toFixed(2),
    				hps: i <= 0
    				? 0
    				: (el.heal / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true)).toFixed(2),
    				additional: el.ability.substring(0, el.ability.indexOf("{"))
    			}));

    			chart.data = dataChart;
    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			// Create chart instance
    			chart = am4core.create("chart-timeline-heal", am4charts.XYChart);

    			chart.scrollbarX = new am4core.Scrollbar();
    			chart.scrollbarY = new am4core.Scrollbar();

    			/* Create axes */
    			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());

    			categoryAxis.dataFields.category = "duration";

    			// categoryAxis.renderer.minGridDistance = 30;
    			/* Create value axis */
    			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

    			valueAxis.min = 0;

    			/* Create series */
    			const columnSeries = chart.series.push(new am4charts.ColumnSeries());

    			columnSeries.name = "Heal";
    			columnSeries.dataFields.valueY = "heal";
    			columnSeries.dataFields.categoryX = "duration";
    			columnSeries.columns.template.tooltipText = "[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]";
    			columnSeries.columns.template.propertyFields.fillOpacity = "fillOpacity";
    			const lineSeries = chart.series.push(new am4charts.LineSeries());
    			lineSeries.name = "HPS";
    			lineSeries.dataFields.valueY = "hps";
    			lineSeries.dataFields.categoryX = "duration";
    			lineSeries.stroke = am4core.color("#347d39");
    			lineSeries.strokeWidth = 3;
    			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
    			bullet.fill = am4core.color("#347d39");
    			bullet.tooltipText = "[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]";
    			const circle = bullet.createChild(am4core.Circle);
    			circle.radius = 3;
    			circle.fill = am4core.color("#fff");
    			circle.strokeWidth = 4;
    			chart.legend = new am4charts.Legend();
    			loadHPSTimeLineReload = updateData();
    		});

    		return updateData;
    	}

    	function loadDTPSTimeLine() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const data = combat.abilitiesTaken.filter(e => e.damage !== null).reduce(
    				(acc, el) => [
    					...acc,
    					{
    						ability: el.ability,
    						timestamp: moment(el.timestamp, MOMENT_FORMAT),
    						rawDamage: el.damage.value,
    						damage: acc.length <= 0
    						? el.damage.value
    						: acc[acc.length - 1].damage + el.damage.value
    					}
    				],
    				[]
    			);

    			const dataChart = data.map((el, i) => ({
    				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true),
    				damage: el.rawDamage.toFixed(2),
    				dtps: i <= 0
    				? 0
    				: (el.damage / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true)).toFixed(2),
    				additional: el.ability.substring(0, el.ability.indexOf("{"))
    			}));

    			chart.data = dataChart;
    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			// Create chart instance
    			chart = am4core.create("chart-timeline-damage-taken", am4charts.XYChart);

    			chart.scrollbarX = new am4core.Scrollbar();
    			chart.scrollbarY = new am4core.Scrollbar();

    			/* Create axes */
    			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());

    			categoryAxis.dataFields.category = "duration";

    			// categoryAxis.renderer.minGridDistance = 30;
    			/* Create value axis */
    			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

    			valueAxis.min = 0;

    			/* Create series */
    			const columnSeries = chart.series.push(new am4charts.ColumnSeries());

    			columnSeries.name = "Damage";
    			columnSeries.dataFields.valueY = "damage";
    			columnSeries.dataFields.categoryX = "duration";
    			columnSeries.columns.template.tooltipText = "[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]";
    			columnSeries.columns.template.propertyFields.fillOpacity = "fillOpacity";
    			const lineSeries = chart.series.push(new am4charts.LineSeries());
    			lineSeries.name = "DTPS";
    			lineSeries.dataFields.valueY = "dtps";
    			lineSeries.dataFields.categoryX = "duration";
    			lineSeries.stroke = am4core.color("#8e1519");
    			lineSeries.strokeWidth = 3;
    			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
    			bullet.fill = am4core.color("#8e1519");
    			bullet.tooltipText = "[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]";
    			const circle = bullet.createChild(am4core.Circle);
    			circle.radius = 3;
    			circle.fill = am4core.color("#fff");
    			circle.strokeWidth = 4;
    			chart.legend = new am4charts.Legend();
    			loadDTPSTimeLineReload = updateData();
    		});

    		return updateData;
    	}

    	function loadTPSTimeLine() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const data = combat.abilitiesApply.filter(e => (e.heal !== null || e.damage !== null) && e.precast === false).reduce(
    				(acc, el) => [
    					...acc,
    					{
    						ability: el.ability,
    						timestamp: moment(el.timestamp, MOMENT_FORMAT),
    						rawThreat: el.threat,
    						threat: acc.length <= 0
    						? el.threat
    						: acc[acc.length - 1].threat + el.threat
    					}
    				],
    				[]
    			);

    			const dataChart = data.map((el, i) => ({
    				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true),
    				threat: el.rawThreat.toFixed(2),
    				tps: i <= 0
    				? 0
    				: (el.threat / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true)).toFixed(2),
    				additional: el.ability.substring(0, el.ability.indexOf("{"))
    			}));

    			chart.data = dataChart;
    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			// Create chart instance
    			chart = am4core.create("chart-timeline-threat", am4charts.XYChart);

    			chart.scrollbarX = new am4core.Scrollbar();
    			chart.scrollbarY = new am4core.Scrollbar();

    			/* Create axes */
    			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());

    			categoryAxis.dataFields.category = "duration";

    			// categoryAxis.renderer.minGridDistance = 30;
    			/* Create value axis */
    			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());

    			valueAxis.min = 0;

    			/* Create series */
    			const columnSeries = chart.series.push(new am4charts.ColumnSeries());

    			columnSeries.name = "Threat";
    			columnSeries.dataFields.valueY = "threat";
    			columnSeries.dataFields.categoryX = "duration";
    			columnSeries.columns.template.tooltipText = "[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]";
    			columnSeries.columns.template.propertyFields.fillOpacity = "fillOpacity";
    			const lineSeries = chart.series.push(new am4charts.LineSeries());
    			lineSeries.name = "TPS";
    			lineSeries.dataFields.valueY = "tps";
    			lineSeries.dataFields.categoryX = "duration";
    			lineSeries.stroke = am4core.color("#966600");
    			lineSeries.strokeWidth = 3;
    			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
    			bullet.fill = am4core.color("#966600");
    			bullet.tooltipText = "[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]";
    			const circle = bullet.createChild(am4core.Circle);
    			circle.radius = 3;
    			circle.fill = am4core.color("#fff");
    			circle.strokeWidth = 4;
    			chart.legend = new am4charts.Legend();
    			loadTPSTimeLineReload = updateData();
    		});

    		return updateData;
    	}

    	function loadHitsGraphic() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const data = combat.abilityTrigger.concat(combat.procsAndTicks);
    			chart.data = data;
    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			chart = am4core.create("chart-hits", am4charts.XYChart);

    			chart.padding(40, 40, 40, 40);
    			const categoryAxis = chart.yAxes.push(new am4charts.CategoryAxis());
    			categoryAxis.renderer.grid.template.location = 0;
    			categoryAxis.dataFields.category = "ability";
    			categoryAxis.renderer.minGridDistance = 1;
    			categoryAxis.renderer.inversed = true;
    			categoryAxis.renderer.grid.template.disabled = true;
    			const valueAxis = chart.xAxes.push(new am4charts.ValueAxis());
    			valueAxis.min = 0;
    			const series = chart.series.push(new am4charts.ColumnSeries());
    			series.dataFields.categoryY = "ability";
    			series.dataFields.valueX = "hits";
    			series.tooltipText = "{valueX.value}";
    			series.columns.template.strokeOpacity = 0;
    			series.columns.template.column.cornerRadiusBottomRight = 5;
    			series.columns.template.column.cornerRadiusTopRight = 5;
    			const labelBullet = series.bullets.push(new am4charts.LabelBullet());
    			labelBullet.label.horizontalCenter = "left";
    			labelBullet.label.dx = 10;
    			labelBullet.label.text = "{values.valueX.workingValue.formatNumber('#.0as')}";
    			labelBullet.locationX = 1;

    			// as by default columns of the same series are of the same color, we add adapter which takes colors from chart.colors color set
    			series.columns.template.adapter.add("fill", function (fill, target) {
    				return chart.colors.getIndex(target.dataItem.index);
    			});

    			categoryAxis.sortBySeries = series;
    			loadHitsGraphicReload = updateData();
    		});

    		return updateData;
    	}

    	function loadGCDPerAbility() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const data = _.mapValues(_.groupBy(combat.gcds, "ability"), x => x.map(ability => _.omit(ability, "ability")));

    			chart.data = Object.keys(data).map(key => ({
    				ability: key,
    				min: Math.min(...data[key].map(x => x.GCD)),
    				median: median(...data[key].map(x => x.GCD)),
    				max: Math.max(...data[key].map(x => x.GCD))
    			}));

    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			chart = am4core.create("chart-gcd-per-ability", am4charts.XYChart);

    			chart.colors.step = 2;
    			chart.legend = new am4charts.Legend();
    			chart.legend.position = "top";
    			chart.legend.paddingBottom = 20;
    			chart.legend.labels.template.maxWidth = 95;
    			const xAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    			xAxis.dataFields.category = "ability";
    			xAxis.renderer.cellStartLocation = 0.1;
    			xAxis.renderer.cellEndLocation = 0.9;
    			xAxis.renderer.grid.template.location = 0;
    			const yAxis = chart.yAxes.push(new am4charts.ValueAxis());
    			yAxis.min = 0;

    			function createSeries(value, name) {
    				const series = chart.series.push(new am4charts.ColumnSeries());
    				series.dataFields.valueY = value;
    				series.dataFields.categoryX = "ability";
    				series.name = name;
    				series.columns.template.tooltipText = "[#000 font-size: 15px]{name} in {categoryX}:\n[/][#000 font-size: 20px]{valueY}[/]";
    				const bullet = series.bullets.push(new am4charts.LabelBullet());
    				bullet.interactionsEnabled = false;
    				bullet.dy = 30;
    				bullet.label.text = "{valueY}";
    				bullet.label.fill = am4core.color("#ffffff");
    				return series;
    			}

    			createSeries("min", "Min");
    			createSeries("median", "Median");
    			createSeries("max", "Max");
    			loadGCDPerAbilityReload = updateData();
    		});

    		return updateData;
    	}

    	function loadGCD_DPSTimeline() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const dpsAndDamage = combat.abilitiesApply.filter(e => e.damage !== null).reduce(
    				(acc, el) => [
    					...acc,
    					{
    						timestamp: moment(el.timestamp, MOMENT_FORMAT),
    						rawDamage: el.damage.value,
    						damage: acc.length <= 0
    						? el.damage.value
    						: acc[acc.length - 1].damage + el.damage.value
    					}
    				],
    				[]
    			);

    			const finalData = combat.gcds.map(e => ({
    				...e,
    				timestamp: moment(e.timestamp, MOMENT_FORMAT)
    			})).map(e => ({
    				ability: e.ability,
    				timestamp: moment(e.timestamp, MOMENT_FORMAT),
    				gcd: e.GCD,
    				damage: dpsAndDamage.filter(e2 => e2.timestamp <= e.timestamp).map(e => e.rawDamage).reduce((acc, el) => acc + el, 0),
    				dps: dpsAndDamage.filter(e2 => e2.timestamp <= e.timestamp).map(e => e.rawDamage).reduce((acc, el) => acc + el, 0) / e.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true)
    			}));

    			chart.data = finalData.map((e, i) => ({
    				count: (i + 1).toString(),
    				gcd: e.gcd,
    				dps: e.dps.toFixed(2),
    				damage: e.damage,
    				additional: e.ability
    			}));

    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			// Create chart instance
    			chart = am4core.create("chart-gcd-timeline-dps", am4charts.XYChart);

    			chart.scrollbarX = new am4core.Scrollbar();
    			chart.scrollbarY = new am4core.Scrollbar();
    			chart.colors.step = 2;

    			// Create axes
    			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());

    			categoryAxis.dataFields.category = "count";
    			categoryAxis.title.text = "GCD Used";

    			// Create series
    			function createAxisAndSeries(field, name, opposite, bullet_type) {
    				const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    				valueAxis.min = 0;

    				if (chart.yAxes.indexOf(valueAxis) != 0) {
    					valueAxis.syncWithAxis = chart.yAxes.getIndex(0);
    				}

    				const series = chart.series.push(new am4charts.LineSeries());
    				series.dataFields.valueY = field;
    				series.dataFields.categoryX = "count";
    				series.strokeWidth = 2;
    				series.yAxis = valueAxis;
    				series.name = name;
    				series.tooltipText = "{name}: [bold]{valueY}[/] [#000]{additional}[/]";
    				series.tensionX = 0.8;
    				series.showOnInit = true;
    				const interfaceColors = new am4core.InterfaceColorSet();

    				switch (bullet_type) {
    					case "triangle":
    						{
    							const bullet = series.bullets.push(new am4charts.Bullet());
    							bullet.width = 12;
    							bullet.height = 12;
    							bullet.horizontalCenter = "middle";
    							bullet.verticalCenter = "middle";
    							const triangle = bullet.createChild(am4core.Triangle);
    							triangle.stroke = interfaceColors.getFor("background");
    							triangle.strokeWidth = 2;
    							triangle.direction = "top";
    							triangle.width = 12;
    							triangle.height = 12;
    						}
    						break;
    					case "rectangle":
    						{
    							const bullet = series.bullets.push(new am4charts.Bullet());
    							bullet.width = 10;
    							bullet.height = 10;
    							bullet.horizontalCenter = "middle";
    							bullet.verticalCenter = "middle";
    							const rectangle = bullet.createChild(am4core.Rectangle);
    							rectangle.stroke = interfaceColors.getFor("background");
    							rectangle.strokeWidth = 2;
    							rectangle.width = 10;
    							rectangle.height = 10;
    						}
    						break;
    					default:
    						{
    							const bullet = series.bullets.push(new am4charts.CircleBullet());
    							bullet.circle.stroke = interfaceColors.getFor("background");
    							bullet.circle.strokeWidth = 2;
    						}
    						break;
    				}

    				valueAxis.renderer.line.strokeOpacity = 1;
    				valueAxis.renderer.line.strokeWidth = 2;
    				valueAxis.renderer.line.stroke = series.stroke;
    				valueAxis.renderer.labels.template.fill = series.stroke;
    				valueAxis.renderer.opposite = opposite;
    			}

    			createAxisAndSeries("gcd", "GCD", false, "circle");
    			createAxisAndSeries("dps", "DPS", true, "triangle");
    			createAxisAndSeries("damage", "Damage", true, "rectangle");

    			// Add legend
    			chart.legend = new am4charts.Legend();

    			chart.cursor = new am4charts.XYCursor();
    			loadGCD_DPSTimelineReload = updateData();
    		});

    		return updateData;
    	}

    	function loadGCD_HPSTimeline() {
    		let chart;

    		const updateData = (dispose = false) => {
    			if (typeof chart === "undefined") return;
    			if (typeof combat === "undefined") return;

    			if (dispose) {
    				chart.dispose();
    				return;
    			}

    			const hpsAndHeal = combat.abilitiesApply.filter(e => e.heal !== null).reduce(
    				(acc, el) => [
    					...acc,
    					{
    						timestamp: moment(el.timestamp, MOMENT_FORMAT),
    						rawHeal: el.heal.value,
    						heal: acc.length <= 0
    						? el.heal.value
    						: acc[acc.length - 1].heal + el.heal.value
    					}
    				],
    				[]
    			);

    			const finalData = combat.gcds.map(e => ({
    				...e,
    				timestamp: moment(e.timestamp, MOMENT_FORMAT)
    			})).map(e => ({
    				ability: e.ability,
    				timestamp: moment(e.timestamp, MOMENT_FORMAT),
    				gcd: e.GCD,
    				heal: hpsAndHeal.filter(e2 => e2.timestamp <= e.timestamp).map(e => e.rawHeal).reduce((acc, el) => acc + el, 0),
    				hps: hpsAndHeal.filter(e2 => e2.timestamp <= e.timestamp).map(e => e.rawHeal).reduce((acc, el) => acc + el, 0) / e.timestamp.diff(moment(combat.start, MOMENT_FORMAT), "seconds", true)
    			}));

    			chart.data = finalData.map((e, i) => ({
    				count: (i + 1).toString(),
    				gcd: e.gcd,
    				hps: e.hps.toFixed(2),
    				heal: e.heal,
    				additional: e.ability
    			}));

    			return updateData;
    		};

    		am4core.ready(function () {
    			// Themes begin
    			am4core.useTheme(am4themes_dark);

    			// Themes end
    			// Create chart instance
    			chart = am4core.create("chart-gcd-timeline-hps", am4charts.XYChart);

    			chart.scrollbarX = new am4core.Scrollbar();
    			chart.scrollbarY = new am4core.Scrollbar();
    			chart.colors.step = 2;

    			// Create axes
    			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());

    			categoryAxis.dataFields.category = "count";
    			categoryAxis.title.text = "GCD Used";

    			// Create series
    			function createAxisAndSeries(field, name, opposite, bullet_type) {
    				const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    				valueAxis.min = 0;

    				if (chart.yAxes.indexOf(valueAxis) != 0) {
    					valueAxis.syncWithAxis = chart.yAxes.getIndex(0);
    				}

    				const series = chart.series.push(new am4charts.LineSeries());
    				series.dataFields.valueY = field;
    				series.dataFields.categoryX = "count";
    				series.strokeWidth = 2;
    				series.yAxis = valueAxis;
    				series.name = name;
    				series.tooltipText = "{name}: [bold]{valueY}[/] [#000]{additional}[/]";
    				series.tensionX = 0.8;
    				series.showOnInit = true;
    				const interfaceColors = new am4core.InterfaceColorSet();

    				switch (bullet_type) {
    					case "triangle":
    						{
    							const bullet = series.bullets.push(new am4charts.Bullet());
    							bullet.width = 12;
    							bullet.height = 12;
    							bullet.horizontalCenter = "middle";
    							bullet.verticalCenter = "middle";
    							const triangle = bullet.createChild(am4core.Triangle);
    							triangle.stroke = interfaceColors.getFor("background");
    							triangle.strokeWidth = 2;
    							triangle.direction = "top";
    							triangle.width = 12;
    							triangle.height = 12;
    						}
    						break;
    					case "rectangle":
    						{
    							const bullet = series.bullets.push(new am4charts.Bullet());
    							bullet.width = 10;
    							bullet.height = 10;
    							bullet.horizontalCenter = "middle";
    							bullet.verticalCenter = "middle";
    							const rectangle = bullet.createChild(am4core.Rectangle);
    							rectangle.stroke = interfaceColors.getFor("background");
    							rectangle.strokeWidth = 2;
    							rectangle.width = 10;
    							rectangle.height = 10;
    						}
    						break;
    					default:
    						{
    							const bullet = series.bullets.push(new am4charts.CircleBullet());
    							bullet.circle.stroke = interfaceColors.getFor("background");
    							bullet.circle.strokeWidth = 2;
    						}
    						break;
    				}

    				valueAxis.renderer.line.strokeOpacity = 1;
    				valueAxis.renderer.line.strokeWidth = 2;
    				valueAxis.renderer.line.stroke = series.stroke;
    				valueAxis.renderer.labels.template.fill = series.stroke;
    				valueAxis.renderer.opposite = opposite;
    			}

    			createAxisAndSeries("gcd", "GCD", false, "circle");
    			createAxisAndSeries("hps", "HPS", true, "triangle");
    			createAxisAndSeries("heal", "Heal", true, "rectangle");

    			// Add legend
    			chart.legend = new am4charts.Legend();

    			chart.cursor = new am4charts.XYCursor();
    			loadGCD_HPSTimelineReload = updateData();
    		});

    		return updateData;
    	}

    	async function toggleParse() {
    		$$invalidate(4, realTimeParse = !realTimeParse);

    		if (realTimeParse) {
    			resetDashboard();
    			$$invalidate(9, noFileSelected = true);
    			await loadFiles();
    			await tick();
    			const _files = files.map(e => parseToDate(e)).filter(e => e !== null).sort((a, b) => a.dateTime.unix() - b.dateTime.unix());

    			if (_files.length > 0) {
    				$$invalidate(3, selectedFile = _files[_files.length - 1].file);
    				parse.postMessage(JSON.stringify({ type: "real_time", path, selectedFile }));
    				ipcRenderer.invoke("start-parse");
    			}
    		} else {
    			parse.postMessage(JSON.stringify({ type: "stop_real_time" }));
    		}
    	}

    	function parseToDate(file) {
    		if (!file.includes("combat_")) {
    			return null;
    		}

    		const name = file.replace("combat_", "").replace(".txt", "").trim();
    		const date = name.substring(0, name.indexOf("_")).trim();
    		const time = name.substring(name.indexOf("_") + 1, name.length).trim();
    		const m = moment(date + " " + time.substring(0, 2) + ":" + time.substring(3, 5) + ":" + time.substring(6, 8) + "." + time.substring(9), MOMENT_FORMAT);
    		return m.isValid() ? { dateTime: m, file } : null;
    	}

    	function interpretCombatFileName(file) {
    		const m = parseToDate(file);
    		return m !== null ? m.dateTime.format("LLLL") : file;
    	}

    	async function loadFiles() {
    		try {
    			$$invalidate(2, files = await fsPromises.readdir(path));
    			$$invalidate(7, logs = {});

    			files.forEach(e => {
    				$$invalidate(7, logs[e] = null, logs);
    			});
    		} catch(err) {
    			console.error(err);
    		}
    	}

    	(async function () {
    		parse.onmessage = async e => {
    			const message = JSON.parse(e.data);

    			switch (message.type) {
    				case "ready_current_combat":
    					{
    						parse.postMessage(JSON.stringify({ type: "current_combat" }));
    					}
    					break;
    				case "current_combat":
    					{
    						parse.postMessage(JSON.stringify({ type: "current_combat" }));

    						if (message.data.length > 0 && message.data[0].id !== currentCombatHasChanges) {
    							currentCombatHasChanges = message.data[0].id;
    							await loadStats(message.data[0]);
    						}
    					}
    					break;
    				case "parse_logs":
    					{
    						const file = message.data.file;
    						$$invalidate(7, logs[file] = message.data.stats, logs);
    						await loadStats(logs[file][0]);
    						$$invalidate(1, loading = false);
    					}
    					break;
    				case "parse_log_percentage":
    					{
    						$$invalidate(8, percentage = message.data);
    					}
    					break;
    			}
    		};

    		await loadFiles();
    	})();

    	const writable_props = [];

    	Object_1.keys($$props).forEach(key => {
    		if (!~writable_props.indexOf(key) && key.slice(0, 2) !== "$$") console_1.warn(`<App> was created with unknown prop '${key}'`);
    	});

    	function loading_1_value_binding(value) {
    		percentage = value;
    		$$invalidate(8, percentage);
    	}

    	const click_handler = () => onSelectedMenuItem("analyze");
    	const click_handler_1 = () => onSelectedMenuItem("settings");

    	function select_change_handler() {
    		selectedFile = select_value(this);
    		$$invalidate(3, selectedFile);
    		$$invalidate(2, files);
    	}

    	const click_handler_2 = async stat => await loadStats(stat);
    	const click_handler_3 = () => $$invalidate(10, selectedTab = "damage");
    	const click_handler_4 = () => $$invalidate(10, selectedTab = "heal");

    	function select_change_handler_1() {
    		selectedFile = select_value(this);
    		$$invalidate(3, selectedFile);
    		$$invalidate(2, files);
    	}

    	$$self.$capture_state = () => ({
    		tick,
    		Loading,
    		Settings,
    		moment,
    		median,
    		_,
    		fs,
    		os,
    		fsPromises,
    		ipcRenderer,
    		parse,
    		MOMENT_FORMAT,
    		loading,
    		path,
    		files,
    		selectedFile,
    		selectedFilePrevious,
    		realTimeParse,
    		loadDPSTimeLineReload,
    		loadHPSTimeLineReload,
    		loadDTPSTimeLineReload,
    		loadTPSTimeLineReload,
    		loadHitsGraphicReload,
    		loadGCDPerAbilityReload,
    		loadGCD_DPSTimelineReload,
    		loadGCD_HPSTimelineReload,
    		combat,
    		currentCombatHasChanges,
    		selectedMenu,
    		logs,
    		percentage,
    		noFileSelected,
    		selectedTab,
    		onSelectedMenuItem,
    		resetDashboard,
    		selectCombat,
    		loadStats,
    		disposeCharts,
    		loadDPSTimeLine,
    		loadHPSTimeLine,
    		loadDTPSTimeLine,
    		loadTPSTimeLine,
    		loadHitsGraphic,
    		loadGCDPerAbility,
    		loadGCD_DPSTimeline,
    		loadGCD_HPSTimeline,
    		toggleParse,
    		parseToDate,
    		interpretCombatFileName,
    		loadFiles
    	});

    	$$self.$inject_state = $$props => {
    		if ("loading" in $$props) $$invalidate(1, loading = $$props.loading);
    		if ("path" in $$props) path = $$props.path;
    		if ("files" in $$props) $$invalidate(2, files = $$props.files);
    		if ("selectedFile" in $$props) $$invalidate(3, selectedFile = $$props.selectedFile);
    		if ("selectedFilePrevious" in $$props) selectedFilePrevious = $$props.selectedFilePrevious;
    		if ("realTimeParse" in $$props) $$invalidate(4, realTimeParse = $$props.realTimeParse);
    		if ("loadDPSTimeLineReload" in $$props) loadDPSTimeLineReload = $$props.loadDPSTimeLineReload;
    		if ("loadHPSTimeLineReload" in $$props) loadHPSTimeLineReload = $$props.loadHPSTimeLineReload;
    		if ("loadDTPSTimeLineReload" in $$props) loadDTPSTimeLineReload = $$props.loadDTPSTimeLineReload;
    		if ("loadTPSTimeLineReload" in $$props) loadTPSTimeLineReload = $$props.loadTPSTimeLineReload;
    		if ("loadHitsGraphicReload" in $$props) loadHitsGraphicReload = $$props.loadHitsGraphicReload;
    		if ("loadGCDPerAbilityReload" in $$props) loadGCDPerAbilityReload = $$props.loadGCDPerAbilityReload;
    		if ("loadGCD_DPSTimelineReload" in $$props) loadGCD_DPSTimelineReload = $$props.loadGCD_DPSTimelineReload;
    		if ("loadGCD_HPSTimelineReload" in $$props) loadGCD_HPSTimelineReload = $$props.loadGCD_HPSTimelineReload;
    		if ("combat" in $$props) $$invalidate(5, combat = $$props.combat);
    		if ("currentCombatHasChanges" in $$props) currentCombatHasChanges = $$props.currentCombatHasChanges;
    		if ("selectedMenu" in $$props) $$invalidate(6, selectedMenu = $$props.selectedMenu);
    		if ("logs" in $$props) $$invalidate(7, logs = $$props.logs);
    		if ("percentage" in $$props) $$invalidate(8, percentage = $$props.percentage);
    		if ("noFileSelected" in $$props) $$invalidate(9, noFileSelected = $$props.noFileSelected);
    		if ("selectedTab" in $$props) $$invalidate(10, selectedTab = $$props.selectedTab);
    	};

    	if ($$props && "$$inject" in $$props) {
    		$$self.$inject_state($$props.$$inject);
    	}

    	return [
    		moment,
    		loading,
    		files,
    		selectedFile,
    		realTimeParse,
    		combat,
    		selectedMenu,
    		logs,
    		percentage,
    		noFileSelected,
    		selectedTab,
    		onSelectedMenuItem,
    		selectCombat,
    		loadStats,
    		toggleParse,
    		interpretCombatFileName,
    		loading_1_value_binding,
    		click_handler,
    		click_handler_1,
    		select_change_handler,
    		click_handler_2,
    		click_handler_3,
    		click_handler_4,
    		select_change_handler_1
    	];
    }

    class App extends SvelteComponentDev {
    	constructor(options) {
    		super(options);
    		init(this, options, instance$2, create_fragment$2, safe_not_equal, {}, [-1, -1, -1]);

    		dispatch_dev("SvelteRegisterComponent", {
    			component: this,
    			tagName: "App",
    			options,
    			id: create_fragment$2.name
    		});
    	}
    }

    const app = new App({
    	target: document.body,
    });

    return app;

}());
//# sourceMappingURL=main_menu.js.map

(self.webpackChunk_N_E=self.webpackChunk_N_E||[]).push([[620],{2548:function(e,t,r){var n=function(){return this}()||Function("return this")(),o=n.regeneratorRuntime&&Object.getOwnPropertyNames(n).indexOf("regeneratorRuntime")>=0,a=o&&n.regeneratorRuntime;if(n.regeneratorRuntime=void 0,e.exports=r(8544),o)n.regeneratorRuntime=a;else try{delete n.regeneratorRuntime}catch(u){n.regeneratorRuntime=void 0}},8544:function(e){!function(t){"use strict";var r,n=Object.prototype,o=n.hasOwnProperty,a="function"===typeof Symbol?Symbol:{},u=a.iterator||"@@iterator",i=a.asyncIterator||"@@asyncIterator",c=a.toStringTag||"@@toStringTag",s=t.regeneratorRuntime;if(s)e.exports=s;else{(s=t.regeneratorRuntime=e.exports).wrap=b;var f="suspendedStart",l="suspendedYield",p="executing",d="completed",h={},y={};y[u]=function(){return this};var m=Object.getPrototypeOf,v=m&&m(m(S([])));v&&v!==n&&o.call(v,u)&&(y=v);var g=_.prototype=x.prototype=Object.create(y);T.prototype=g.constructor=_,_.constructor=T,_[c]=T.displayName="GeneratorFunction",s.isGeneratorFunction=function(e){var t="function"===typeof e&&e.constructor;return!!t&&(t===T||"GeneratorFunction"===(t.displayName||t.name))},s.mark=function(e){return Object.setPrototypeOf?Object.setPrototypeOf(e,_):(e.__proto__=_,c in e||(e[c]="GeneratorFunction")),e.prototype=Object.create(g),e},s.awrap=function(e){return{__await:e}},k(E.prototype),E.prototype[i]=function(){return this},s.AsyncIterator=E,s.async=function(e,t,r,n){var o=new E(b(e,t,r,n));return s.isGeneratorFunction(t)?o:o.next().then((function(e){return e.done?e.value:o.next()}))},k(g),g[c]="Generator",g[u]=function(){return this},g.toString=function(){return"[object Generator]"},s.keys=function(e){var t=[];for(var r in e)t.push(r);return t.reverse(),function r(){for(;t.length;){var n=t.pop();if(n in e)return r.value=n,r.done=!1,r}return r.done=!0,r}},s.values=S,P.prototype={constructor:P,reset:function(e){if(this.prev=0,this.next=0,this.sent=this._sent=r,this.done=!1,this.delegate=null,this.method="next",this.arg=r,this.tryEntries.forEach(j),!e)for(var t in this)"t"===t.charAt(0)&&o.call(this,t)&&!isNaN(+t.slice(1))&&(this[t]=r)},stop:function(){this.done=!0;var e=this.tryEntries[0].completion;if("throw"===e.type)throw e.arg;return this.rval},dispatchException:function(e){if(this.done)throw e;var t=this;function n(n,o){return i.type="throw",i.arg=e,t.next=n,o&&(t.method="next",t.arg=r),!!o}for(var a=this.tryEntries.length-1;a>=0;--a){var u=this.tryEntries[a],i=u.completion;if("root"===u.tryLoc)return n("end");if(u.tryLoc<=this.prev){var c=o.call(u,"catchLoc"),s=o.call(u,"finallyLoc");if(c&&s){if(this.prev<u.catchLoc)return n(u.catchLoc,!0);if(this.prev<u.finallyLoc)return n(u.finallyLoc)}else if(c){if(this.prev<u.catchLoc)return n(u.catchLoc,!0)}else{if(!s)throw new Error("try statement without catch or finally");if(this.prev<u.finallyLoc)return n(u.finallyLoc)}}}},abrupt:function(e,t){for(var r=this.tryEntries.length-1;r>=0;--r){var n=this.tryEntries[r];if(n.tryLoc<=this.prev&&o.call(n,"finallyLoc")&&this.prev<n.finallyLoc){var a=n;break}}a&&("break"===e||"continue"===e)&&a.tryLoc<=t&&t<=a.finallyLoc&&(a=null);var u=a?a.completion:{};return u.type=e,u.arg=t,a?(this.method="next",this.next=a.finallyLoc,h):this.complete(u)},complete:function(e,t){if("throw"===e.type)throw e.arg;return"break"===e.type||"continue"===e.type?this.next=e.arg:"return"===e.type?(this.rval=this.arg=e.arg,this.method="return",this.next="end"):"normal"===e.type&&t&&(this.next=t),h},finish:function(e){for(var t=this.tryEntries.length-1;t>=0;--t){var r=this.tryEntries[t];if(r.finallyLoc===e)return this.complete(r.completion,r.afterLoc),j(r),h}},catch:function(e){for(var t=this.tryEntries.length-1;t>=0;--t){var r=this.tryEntries[t];if(r.tryLoc===e){var n=r.completion;if("throw"===n.type){var o=n.arg;j(r)}return o}}throw new Error("illegal catch attempt")},delegateYield:function(e,t,n){return this.delegate={iterator:S(e),resultName:t,nextLoc:n},"next"===this.method&&(this.arg=r),h}}}function b(e,t,r,n){var o=t&&t.prototype instanceof x?t:x,a=Object.create(o.prototype),u=new P(n||[]);return a._invoke=function(e,t,r){var n=f;return function(o,a){if(n===p)throw new Error("Generator is already running");if(n===d){if("throw"===o)throw a;return L()}for(r.method=o,r.arg=a;;){var u=r.delegate;if(u){var i=O(u,r);if(i){if(i===h)continue;return i}}if("next"===r.method)r.sent=r._sent=r.arg;else if("throw"===r.method){if(n===f)throw n=d,r.arg;r.dispatchException(r.arg)}else"return"===r.method&&r.abrupt("return",r.arg);n=p;var c=w(e,t,r);if("normal"===c.type){if(n=r.done?d:l,c.arg===h)continue;return{value:c.arg,done:r.done}}"throw"===c.type&&(n=d,r.method="throw",r.arg=c.arg)}}}(e,r,u),a}function w(e,t,r){try{return{type:"normal",arg:e.call(t,r)}}catch(n){return{type:"throw",arg:n}}}function x(){}function T(){}function _(){}function k(e){["next","throw","return"].forEach((function(t){e[t]=function(e){return this._invoke(t,e)}}))}function E(e){function t(r,n,a,u){var i=w(e[r],e,n);if("throw"!==i.type){var c=i.arg,s=c.value;return s&&"object"===typeof s&&o.call(s,"__await")?Promise.resolve(s.__await).then((function(e){t("next",e,a,u)}),(function(e){t("throw",e,a,u)})):Promise.resolve(s).then((function(e){c.value=e,a(c)}),u)}u(i.arg)}var r;this._invoke=function(e,n){function o(){return new Promise((function(r,o){t(e,n,r,o)}))}return r=r?r.then(o,o):o()}}function O(e,t){var n=e.iterator[t.method];if(n===r){if(t.delegate=null,"throw"===t.method){if(e.iterator.return&&(t.method="return",t.arg=r,O(e,t),"throw"===t.method))return h;t.method="throw",t.arg=new TypeError("The iterator does not provide a 'throw' method")}return h}var o=w(n,e.iterator,t.arg);if("throw"===o.type)return t.method="throw",t.arg=o.arg,t.delegate=null,h;var a=o.arg;return a?a.done?(t[e.resultName]=a.value,t.next=e.nextLoc,"return"!==t.method&&(t.method="next",t.arg=r),t.delegate=null,h):a:(t.method="throw",t.arg=new TypeError("iterator result is not an object"),t.delegate=null,h)}function N(e){var t={tryLoc:e[0]};1 in e&&(t.catchLoc=e[1]),2 in e&&(t.finallyLoc=e[2],t.afterLoc=e[3]),this.tryEntries.push(t)}function j(e){var t=e.completion||{};t.type="normal",delete t.arg,e.completion=t}function P(e){this.tryEntries=[{tryLoc:"root"}],e.forEach(N,this),this.reset(!0)}function S(e){if(e){var t=e[u];if(t)return t.call(e);if("function"===typeof e.next)return e;if(!isNaN(e.length)){var n=-1,a=function t(){for(;++n<e.length;)if(o.call(e,n))return t.value=e[n],t.done=!1,t;return t.value=r,t.done=!0,t};return a.next=a}}return{next:L}}function L(){return{value:r,done:!0}}}(function(){return this}()||Function("return this")())},5860:function(e,t,r){e.exports=r(2548)},2167:function(e,t,r){"use strict";var n=r(3038);var o,a=(o=r(7294))&&o.__esModule?o:{default:o},u=r(1063),i=r(4651),c=r(7426);var s={};function f(e,t,r,n){if(e&&u.isLocalURL(t)){e.prefetch(t,r,n).catch((function(e){0}));var o=n&&"undefined"!==typeof n.locale?n.locale:e&&e.locale;s[t+"%"+r+(o?"%"+o:"")]=!0}}},7426:function(e,t,r){"use strict";var n=r(3038);Object.defineProperty(t,"__esModule",{value:!0}),t.useIntersection=function(e){var t=e.rootMargin,r=e.disabled||!u,c=o.useRef(),s=o.useState(!1),f=n(s,2),l=f[0],p=f[1],d=o.useCallback((function(e){c.current&&(c.current(),c.current=void 0),r||l||e&&e.tagName&&(c.current=function(e,t,r){var n=function(e){var t=e.rootMargin||"",r=i.get(t);if(r)return r;var n=new Map,o=new IntersectionObserver((function(e){e.forEach((function(e){var t=n.get(e.target),r=e.isIntersecting||e.intersectionRatio>0;t&&r&&t(r)}))}),e);return i.set(t,r={id:t,observer:o,elements:n}),r}(r),o=n.id,a=n.observer,u=n.elements;return u.set(e,t),a.observe(e),function(){u.delete(e),a.unobserve(e),0===u.size&&(a.disconnect(),i.delete(o))}}(e,(function(e){return e&&p(e)}),{rootMargin:t}))}),[r,t,l]);return o.useEffect((function(){if(!u&&!l){var e=a.requestIdleCallback((function(){return p(!0)}));return function(){return a.cancelIdleCallback(e)}}}),[l]),[d,l]};var o=r(7294),a=r(3447),u="undefined"!==typeof IntersectionObserver;var i=new Map},1664:function(e,t,r){r(2167)},75:function(e,t,r){var n=r(4155);(function(){var t,r,o,a,u,i;"undefined"!==typeof performance&&null!==performance&&performance.now?e.exports=function(){return performance.now()}:"undefined"!==typeof n&&null!==n&&n.hrtime?(e.exports=function(){return(t()-u)/1e6},r=n.hrtime,a=(t=function(){var e;return 1e9*(e=r())[0]+e[1]})(),i=1e9*n.uptime(),u=a-i):Date.now?(e.exports=function(){return Date.now()-o},o=Date.now()):(e.exports=function(){return(new Date).getTime()-o},o=(new Date).getTime())}).call(this)},4155:function(e){var t,r,n=e.exports={};function o(){throw new Error("setTimeout has not been defined")}function a(){throw new Error("clearTimeout has not been defined")}function u(e){if(t===setTimeout)return setTimeout(e,0);if((t===o||!t)&&setTimeout)return t=setTimeout,setTimeout(e,0);try{return t(e,0)}catch(r){try{return t.call(null,e,0)}catch(r){return t.call(this,e,0)}}}!function(){try{t="function"===typeof setTimeout?setTimeout:o}catch(e){t=o}try{r="function"===typeof clearTimeout?clearTimeout:a}catch(e){r=a}}();var i,c=[],s=!1,f=-1;function l(){s&&i&&(s=!1,i.length?c=i.concat(c):f=-1,c.length&&p())}function p(){if(!s){var e=u(l);s=!0;for(var t=c.length;t;){for(i=c,c=[];++f<t;)i&&i[f].run();f=-1,t=c.length}i=null,s=!1,function(e){if(r===clearTimeout)return clearTimeout(e);if((r===a||!r)&&clearTimeout)return r=clearTimeout,clearTimeout(e);try{r(e)}catch(t){try{return r.call(null,e)}catch(t){return r.call(this,e)}}}(e)}}function d(e,t){this.fun=e,this.array=t}function h(){}n.nextTick=function(e){var t=new Array(arguments.length-1);if(arguments.length>1)for(var r=1;r<arguments.length;r++)t[r-1]=arguments[r];c.push(new d(e,t)),1!==c.length||s||u(p)},d.prototype.run=function(){this.fun.apply(null,this.array)},n.title="browser",n.browser=!0,n.env={},n.argv=[],n.version="",n.versions={},n.on=h,n.addListener=h,n.once=h,n.off=h,n.removeListener=h,n.removeAllListeners=h,n.emit=h,n.prependListener=h,n.prependOnceListener=h,n.listeners=function(e){return[]},n.binding=function(e){throw new Error("process.binding is not supported")},n.cwd=function(){return"/"},n.chdir=function(e){throw new Error("process.chdir is not supported")},n.umask=function(){return 0}},4087:function(e,t,r){for(var n=r(75),o="undefined"===typeof window?r.g:window,a=["moz","webkit"],u="AnimationFrame",i=o["request"+u],c=o["cancel"+u]||o["cancelRequest"+u],s=0;!i&&s<a.length;s++)i=o[a[s]+"Request"+u],c=o[a[s]+"Cancel"+u]||o[a[s]+"CancelRequest"+u];if(!i||!c){var f=0,l=0,p=[];i=function(e){if(0===p.length){var t=n(),r=Math.max(0,16.666666666666668-(t-f));f=r+t,setTimeout((function(){var e=p.slice(0);p.length=0;for(var t=0;t<e.length;t++)if(!e[t].cancelled)try{e[t].callback(f)}catch(r){setTimeout((function(){throw r}),0)}}),Math.round(r))}return p.push({handle:++l,callback:e,cancelled:!1}),l},c=function(e){for(var t=0;t<p.length;t++)p[t].handle===e&&(p[t].cancelled=!0)}}e.exports=function(e){return i.call(o,e)},e.exports.cancel=function(){c.apply(o,arguments)},e.exports.polyfill=function(e){e||(e=o),e.requestAnimationFrame=i,e.cancelAnimationFrame=c}},3679:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,o=r(7294),a=(n=o)&&n.__esModule?n:{default:n};var u=function(){return a.default.createElement("noscript",null)};u.updateCursor=function(e,t){var r=t.speed,n=t.count,o=t.delay;return Object.assign({},e,{numToErase:n,preEraseLineNum:e.lineNum,speed:r>0?r:e.speed,delay:o>0?e.delay+o:e.delay})},u.getName=function(){return"Backspace"},t.default=u},288:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=f(["\n  from, to {\n    opacity: 1;\n  }\n  50% {\n    opacity: 0;\n  }\n"],["\n  from, to {\n    opacity: 1;\n  }\n  50% {\n    opacity: 0;\n  }\n"]),o=f(["\n  font-weight: 100;\n  color: black;\n  font-size: 1em;\n  padding-left: 2px;\n  animation: "," 1s step-end infinite;\n"],["\n  font-weight: 100;\n  color: black;\n  font-size: 1em;\n  padding-left: 2px;\n  animation: "," 1s step-end infinite;\n"]),a=s(r(7294)),u=s(r(5697)),i=r(4404),c=s(i);function s(e){return e&&e.__esModule?e:{default:e}}function f(e,t){return Object.freeze(Object.defineProperties(e,{raw:{value:Object.freeze(t)}}))}var l=(0,i.keyframes)(n),p=c.default.span(o,l),d=function(e){var t=e.className;return a.default.createElement(p,{className:t},"|")};d.propTypes={className:u.default.string},d.defaultProps={className:""},t.default=d},6251:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,o=r(7294),a=(n=o)&&n.__esModule?n:{default:n};var u=function(){return a.default.createElement("noscript",null)};u.updateCursor=function(e,t){var r=t.ms;return Object.assign({},e,{delay:e.delay+r})},u.getName=function(){return"Delay"},t.default=u},348:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,o=r(7294),a=(n=o)&&n.__esModule?n:{default:n};var u=function(){return a.default.createElement("noscript",null)};u.updateCursor=function(e,t){var r=t.count,n=t.delay,o=t.speed;return Object.assign({},e,{numToErase:r,preEraseLineNum:e.lineNum,speed:o>0?o:e.speed,delay:n>0?e.delay+n:e.delay,step:"line"})},u.getName=function(){return"Reset"},t.default=u},2338:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n,o=r(7294),a=(n=o)&&n.__esModule?n:{default:n};var u=function(){return a.default.createElement("noscript",null)};u.updateCursor=function(e,t){var r=t.ms;return Object.assign({},e,{speed:r})},u.getName=function(){return"Speed"},t.default=u},960:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=y(r(5860)),o=function(){function e(e,t){for(var r=0;r<t.length;r++){var n=t[r];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(e,n.key,n)}}return function(t,r,n){return r&&e(t.prototype,r),n&&e(t,n),t}}(),a=r(7294),u=y(a),i=y(r(5697)),c=y(r(4087)),s=r(3631),f=y(r(3679)),l=y(r(348)),p=y(r(6251)),d=y(r(2338)),h=y(r(288));function y(e){return e&&e.__esModule?e:{default:e}}function m(e){if(Array.isArray(e)){for(var t=0,r=Array(e.length);t<e.length;t++)r[t]=e[t];return r}return Array.from(e)}function v(e){return function(){var t=e.apply(this,arguments);return new Promise((function(e,r){return function n(o,a){try{var u=t[o](a),i=u.value}catch(c){return void r(c)}if(!u.done)return Promise.resolve(i).then((function(e){n("next",e)}),(function(e){n("throw",e)}));e(i)}("next")}))}}function g(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function b(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!==typeof t&&"function"!==typeof t?e:t}var w=function(e){function t(){var e,r,o,a=this;g(this,t);for(var u=arguments.length,i=Array(u),c=0;c<u;c++)i[c]=arguments[c];return r=o=b(this,(e=t.__proto__||Object.getPrototypeOf(t)).call.apply(e,[this].concat(i))),o.state={isFinished:!1,text:[]},o.updateState=function(){var e=v(n.default.mark((function e(t){return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(!o.hasMounted){e.next=2;break}return e.abrupt("return",new Promise((function(e){o.setState(t,e)})));case 2:case"end":return e.stop()}}),e,a)})));return function(t){return e.apply(this,arguments)}}(),o.resetState=v(n.default.mark((function e(){return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",o.updateState({toType:(0,s.extractText)(o.props.children),cursor:{lineNum:0,charPos:0,numToErase:0,preEraseLineNum:0,delay:o.props.startDelay,speed:o.props.speed,step:"char"}}));case 1:case"end":return e.stop()}}),e,a)}))),o.beginTyping=v(n.default.mark((function e(){var t;return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(t=Object.assign({},o.state.cursor),!(o.state.toType.length>0||t.numToErase>0)){e.next=10;break}return e.next=4,o.props.onBeforeType(o.state.text);case 4:return e.next=6,o.type();case 6:return e.next=8,o.props.onAfterType(o.state.text);case 8:e.next=18;break;case 10:return e.next=12,o.props.onFinishedTyping();case 12:if(!o.props.loop){e.next=17;break}return e.next=15,o.resetState();case 15:e.next=18;break;case 17:return e.abrupt("return",o.updateState({isFinished:!0}));case 18:if(!o.hasMounted){e.next=20;break}return e.abrupt("return",o.beginTyping());case 20:case"end":return e.stop()}}),e,a)}))),o.type=v(n.default.mark((function e(){var t,r;return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:for(t=[].concat(m(o.state.toType)),r=Object.assign({},o.state.cursor);t&&t[0]&&t[0].type&&t[0].type.updateCursor&&r.numToErase<1;)r=t[0].type.updateCursor(r,t[0].props),t.shift();return e.next=5,o.updateState({cursor:r,toType:t});case 5:return e.abrupt("return",o.animateNextStep());case 6:case"end":return e.stop()}}),e,a)}))),o.animateNextStep=v(n.default.mark((function e(){return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",new Promise((function(e){setTimeout(v(n.default.mark((function t(){var r,u,i;return n.default.wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return r=o.state,u=r.cursor,i=r.toType,t.next=3,o.updateState({cursor:Object.assign({},u,{delay:0})});case 3:if(!("char"===u.step&&u.numToErase<1)){t.next=9;break}if(!(i.length>0)){t.next=7;break}return t.next=7,o.typeCharacter();case 7:t.next=11;break;case 9:return t.next=11,o.erase();case 11:e();case 12:case"end":return t.stop()}}),t,a)}))),o.state.cursor.delay)})));case 1:case"end":return e.stop()}}),e,a)}))),o.typeCharacter=v(n.default.mark((function e(){return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",new Promise(function(){var e=v(n.default.mark((function e(t){var r,u,i;return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return r=[].concat(m(o.state.toType)),u=[].concat(m(o.state.text)),i=Object.assign({},o.state.cursor),u.length-1<i.lineNum&&(u[i.lineNum]=""),u[i.lineNum]+=r[0][i.charPos],i.charPos+=1,r[0].length-1<i.charPos&&(i.lineNum+=1,i.charPos=0,r.shift()),e.next=9,o.updateState({cursor:i,text:u,toType:r});case 9:setTimeout(t,(0,s.randomize)(i.speed));case 10:case"end":return e.stop()}}),e,a)})));return function(t){return e.apply(this,arguments)}}()));case 1:case"end":return e.stop()}}),e,a)}))),o.erase=v(n.default.mark((function e(){return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",new Promise(function(){var e=v(n.default.mark((function e(t){var r,u;return n.default.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:r=[].concat(m(o.state.text)),u=Object.assign({},o.state.cursor);case 2:if(!(u.lineNum>r.length-1||u.charPos<0)){e.next=9;break}if(u.lineNum-=1,!(u.lineNum<0)){e.next=6;break}return e.abrupt("break",9);case 6:u.charPos=r[u.lineNum].length?r[u.lineNum].length-1:0,e.next=2;break;case 9:return"char"===u.step&&u.lineNum>=0?r[u.lineNum]=r[u.lineNum].substr(0,r[u.lineNum].length-1):u.numToErase>0?r[u.lineNum]="":r.length=0,u.charPos-=1,u.numToErase-=1,u.numToErase<1&&(u.lineNum=u.preEraseLineNum,u.charPos=0,u.step="char"),e.next=15,o.updateState({cursor:u,text:r});case 15:setTimeout(t,(0,s.randomize)(u.speed));case 16:case"end":return e.stop()}}),e,a)})));return function(t){return e.apply(this,arguments)}}()));case 1:case"end":return e.stop()}}),e,a)}))),b(o,r)}return function(e,t){if("function"!==typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}(t,e),o(t,[{key:"componentDidUpdate",value:function(e){var t=this.props.children;void 0!==t&&JSON.stringify(t,(0,s.getCircularReplacer)())!==JSON.stringify(e.children,(0,s.getCircularReplacer)())&&this.resetState()}},{key:"componentDidMount",value:function(){var e=this;this.hasMounted=!0,this.resetState().then(v(n.default.mark((function t(){return n.default.wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,e.props.onStartedTyping();case 2:(0,c.default)(e.beginTyping);case 3:case"end":return t.stop()}}),t,e)}))))}},{key:"componentWillUnmount",value:function(){this.hasMounted=!1}},{key:"render",value:function(){var e=this.props,t=e.children,r=e.className,n=e.cursorClassName,o=e.hideCursor,a=this.state,i=a.isFinished,c=a.text,f=this.props.cursor||u.default.createElement(h.default,{className:n}),l=(0,s.replaceTreeText)(t,c,f,i||o);return u.default.createElement("div",{className:r},l)}}]),t}(a.Component);w.propTypes={children:i.default.node.isRequired,className:i.default.string,cursor:i.default.node,cursorClassName:i.default.string,speed:i.default.number,startDelay:i.default.number,loop:i.default.bool,onStartedTyping:i.default.func,onBeforeType:i.default.func,onAfterType:i.default.func,onFinishedTyping:i.default.func},w.defaultProps={className:"",cursorClassName:"",speed:50,startDelay:0,loop:!1,onStartedTyping:function(){},onBeforeType:function(){},onAfterType:function(){},onFinishedTyping:function(){}},w.Backspace=f.default,w.Reset=l.default,w.Delay=p.default,w.Speed=d.default,w.Cursor=h.default,t.default=w},6342:function(e,t,r){"use strict";var n=r(3679);var o=r(288);var a=r(6251);var u=r(348);var i=r(2338);var c=s(r(960));function s(e){return e&&e.__esModule?e:{default:e}}t.ZP=c.default},3631:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.getCircularReplacer=t.replaceTreeText=t.extractText=t.randomize=t.gaussianRandomInRange=t.randomInRange=void 0;var n="function"===typeof Symbol&&"symbol"===typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"===typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},o=r(7294),a=i(o),u=i(r(4670));function i(e){return e&&e.__esModule?e:{default:e}}var c=["area","base","br","col","command","embed","hr","img","input","keygen","link","meta","param","source","track","wbr"],s=function e(t){return t.reduce((function(t,r){return t.concat(Array.isArray(r)?o.Children.toArray(e(r)):o.Children.toArray(r))}),[])},f=function(e){return e.filter((function(e){return void 0!==e}))},l=function(e){return["Backspace","Delay","Speed","Reset"].some((function(t){return e.type&&e.type.getName&&e.type.getName()===t}))},p=t.randomInRange=function(e,t){return Math.floor(Math.random()*(t-e+1))+e},d=t.gaussianRandomInRange=function(e,t){for(var r=p(e,t),n=0;n<5;n++)r+=p(e,t);return Math.floor(r/6)};t.randomize=function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:.2;return d(e+e*t,e-e*t)},t.extractText=function(){var e=function e(t){return l(t)?t:a.default.isValidElement(t)?-1!==c.indexOf(t.type)?"\n":o.Children.map(t.props.children,(function(t){return e(t)})):Array.isArray(t)?t.map((function(t){return e(t)})):String(t)},t=e.apply(void 0,arguments);return Array.isArray(t)?f(s(t)):f([t])},t.replaceTreeText=function(e,t,r,n){return function e(t,i){if(!(i.length<1)&&!l(t))return a.default.isValidElement(t)?-1!==c.indexOf(t.type)?1===i.length?o.Children.toArray([""===i.shift()?void 0:t,n?null:r]):""===i.shift()?void 0:t:a.default.createElement(t.type,Object.assign({},t.props,{key:t.key||"Typing."+u.default.generate()}),f(o.Children.toArray(t.props.children).map((function(t){return e(t,i)})))):Array.isArray(t)?f(t.map((function(t){return e(t,i)}))):1===i.length?o.Children.toArray([i.shift(),n?null:r]):i.shift()||""}(e,t.slice())},t.getCircularReplacer=function(){var e=new WeakSet;return function(t,r){if("object"===("undefined"===typeof r?"undefined":n(r))&&null!==r){if(e.has(r))return;try{e.add(r)}catch(o){}}return r}}},4670:function(e,t,r){"use strict";e.exports=r(5607)},9829:function(e,t,r){"use strict";var n,o,a,u=r(8946),i="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-";function c(){a=!1}function s(e){if(e){if(e!==n){if(e.length!==i.length)throw new Error("Custom alphabet for shortid must be "+i.length+" unique characters. You submitted "+e.length+" characters: "+e);var t=e.split("").filter((function(e,t,r){return t!==r.lastIndexOf(e)}));if(t.length)throw new Error("Custom alphabet for shortid must be "+i.length+" unique characters. These characters were not unique: "+t.join(", "));n=e,c()}}else n!==i&&(n=i,c())}function f(){return a||(a=function(){n||s(i);for(var e,t=n.split(""),r=[],o=u.nextValue();t.length>0;)o=u.nextValue(),e=Math.floor(o*t.length),r.push(t.splice(e,1)[0]);return r.join("")}())}e.exports={get:function(){return n||i},characters:function(e){return s(e),n},seed:function(e){u.seed(e),o!==e&&(c(),o=e)},lookup:function(e){return f()[e]},shuffled:f}},480:function(e,t,r){"use strict";var n,o,a=r(8416);r(9829);e.exports=function(e){var t="",r=Math.floor(.001*(Date.now()-1567752802062));return r===o?n++:(n=0,o=r),t+=a(7),t+=a(e),n>0&&(t+=a(n)),t+=a(r)}},8416:function(e,t,r){"use strict";var n=r(9829),o=r(3766),a=r(296);e.exports=function(e){for(var t,r=0,u="";!t;)u+=a(o,n.get(),1),t=e<Math.pow(16,r+1),r++;return u}},5607:function(e,t,r){"use strict";var n=r(9829),o=r(480),a=r(1082),u=r(5636)||0;function i(){return o(u)}e.exports=i,e.exports.generate=i,e.exports.seed=function(t){return n.seed(t),e.exports},e.exports.worker=function(t){return u=t,e.exports},e.exports.characters=function(e){return void 0!==e&&n.characters(e),n.shuffled()},e.exports.isValid=a},1082:function(e,t,r){"use strict";var n=r(9829);e.exports=function(e){return!(!e||"string"!==typeof e||e.length<6)&&!new RegExp("[^"+n.get().replace(/[|\\{}()[\]^$+*?.-]/g,"\\$&")+"]").test(e)}},3766:function(e){"use strict";var t,r="object"===typeof window&&(window.crypto||window.msCrypto);t=r&&r.getRandomValues?function(e){return r.getRandomValues(new Uint8Array(e))}:function(e){for(var t=[],r=0;r<e;r++)t.push(Math.floor(256*Math.random()));return t},e.exports=t},8946:function(e){"use strict";var t=1;e.exports={nextValue:function(){return(t=(9301*t+49297)%233280)/233280},seed:function(e){t=e}}},5636:function(e){"use strict";e.exports=0},296:function(e){e.exports=function(e,t,r){for(var n=(2<<Math.log(t.length-1)/Math.LN2)-1,o=-~(1.6*n*r/t.length),a="";;)for(var u=e(o),i=o;i--;)if((a+=t[u[i]&n]||"").length===+r)return a}},7326:function(e,t,r){"use strict";function n(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}r.d(t,{Z:function(){return n}})},5671:function(e,t,r){"use strict";function n(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}r.d(t,{Z:function(){return n}})},3144:function(e,t,r){"use strict";function n(e,t){for(var r=0;r<t.length;r++){var n=t[r];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(e,n.key,n)}}function o(e,t,r){return t&&n(e.prototype,t),r&&n(e,r),e}r.d(t,{Z:function(){return o}})},1120:function(e,t,r){"use strict";function n(e){return(n=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)})(e)}r.d(t,{Z:function(){return n}})},9340:function(e,t,r){"use strict";function n(e,t){return(n=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,t)}function o(e,t){if("function"!==typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),t&&n(e,t)}r.d(t,{Z:function(){return o}})},6215:function(e,t,r){"use strict";function n(e){return(n="function"===typeof Symbol&&"symbol"===typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"===typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e})(e)}r.d(t,{Z:function(){return a}});var o=r(7326);function a(e,t){if(t&&("object"===n(t)||"function"===typeof t))return t;if(void 0!==t)throw new TypeError("Derived constructors may only return object or undefined");return(0,o.Z)(e)}}}]);
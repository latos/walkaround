function rUb(){}
function Jzc(){}
function azc(){bzc.call(this,false)}
function Ezc(a,b){Gzc.call(this,a,false);this.c=b}
function Fzc(a,b){Gzc.call(this,a,false);Czc(this,b)}
function Dzc(a){Gzc.call(this,'GWT',true);Czc(this,a)}
function sUb(a){this.d=a;this.c=CEb(this.d.b)}
function Gyc(a,b){return Nyc(a,b,a.b.c)}
function Nyc(a,b,c){if(c<0||c>a.b.c){throw new $Qc}a.p&&(b.R[E8c]=2,undefined);Fyc(a,c,b.R);cYc(a.b,c,b);return b}
function Czc(a,b){a.e=b;!!a.d&&_yc(a.d,a);if(b){b.R.tabIndex=-1;a.R.setAttribute(M9c,a5c)}else{a.R.setAttribute(M9c,v6c)}}
function yEb(a){var b,c;b=pgb(a.b.Vd(E9c),138);if(b==null){c=fgb(ezb,{124:1,135:1,138:1},1,['New','Open',F9c,G9c,'Exit']);a.b.Xd(E9c,c);return c}else{return b}}
function xEb(a){var b,c;b=pgb(a.b.Vd(D9c),138);if(b==null){c=fgb(ezb,{124:1,135:1,138:1},1,['Undo','Redo','Cut','Copy','Paste']);a.b.Xd(D9c,c);return c}else{return b}}
function BEb(a){var b,c;b=pgb(a.b.Vd(J9c),138);if(b==null){c=fgb(ezb,{124:1,135:1,138:1},1,['Contents','Fortune Cookie','About GWT']);a.b.Xd(J9c,c);return c}else{return b}}
function AEb(a){var b,c;b=pgb(a.b.Vd(I9c),138);if(b==null){c=fgb(ezb,{124:1,135:1,138:1},1,['Download','Examples',K4c,"GWT wit' the program"]);a.b.Xd(I9c,c);return c}else{return b}}
function zEb(a){var b,c;b=pgb(a.b.Vd(H9c),138);if(b==null){c=fgb(ezb,{124:1,135:1,138:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.Xd(H9c,c);return c}else{return b}}
function Kzc(){var a;ke(this,$doc.createElement(l6c));this.R[c2c]='gwt-MenuItemSeparator';a=$doc.createElement(h2c);kl(this.R,YBc(a));a[c2c]='menuSeparatorInner'}
function CEb(a){var b,c;b=pgb(a.b.Vd(K9c),138);if(b==null){c=fgb(ezb,{124:1,135:1,138:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.Xd(K9c,c);return c}else{return b}}
function nUb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new sUb(a);n=new azc;n.c=true;n.R.style[d2c]='500px';n.f=true;q=new bzc(true);p=zEb(a.b);for(k=0;k<p.length;++k){Eyc(q,new Ezc(p[k],o))}d=new bzc(true);d.f=true;Eyc(n,new Fzc('File',d));e=yEb(a.b);for(k=0;k<e.length;++k){if(k==3){Gyc(d,new Kzc);Eyc(d,new Fzc(e[3],q));Gyc(d,new Kzc)}else{Eyc(d,new Ezc(e[k],o))}}b=new bzc(true);Eyc(n,new Fzc('Edit',b));c=xEb(a.b);for(k=0;k<c.length;++k){Eyc(b,new Ezc(c[k],o))}f=new bzc(true);Eyc(n,new Dzc(f));g=AEb(a.b);for(k=0;k<g.length;++k){Eyc(f,new Ezc(g[k],o))}i=new bzc(true);Gyc(n,new Kzc);Eyc(n,new Fzc('Help',i));j=BEb(a.b);for(k=0;k<j.length;++k){Eyc(i,new Ezc(j[k],o))}mJc(n.R,a2c,L9c);$yc(n,L9c);return n}
var M9c='aria-haspopup',L9c='cwMenuBar',D9c='cwMenuBarEditOptions',E9c='cwMenuBarFileOptions',H9c='cwMenuBarFileRecents',I9c='cwMenuBarGWTOptions',J9c='cwMenuBarHelpOptions',K9c='cwMenuBarPrompts';_=sUb.prototype=rUb.prototype=new Y;_.dc=function tUb(){Ikc(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function uUb(){return Oob};_.cM={82:1};_.b=0;_.d=null;_=vUb.prototype;_.bc=function zUb(){$Cb(this.c,nUb(this.b))};_=azc.prototype=Dyc.prototype;_=Fzc.prototype=Ezc.prototype=Dzc.prototype=yzc.prototype;_=Kzc.prototype=Jzc.prototype=new fe;_.gC=function Lzc(){return sub};_.cM={91:1,97:1,110:1};var Oob=HRc(u7c,'CwMenuBar$1'),sub=HRc(Y6c,'MenuItemSeparator');$1c(tj)(18);
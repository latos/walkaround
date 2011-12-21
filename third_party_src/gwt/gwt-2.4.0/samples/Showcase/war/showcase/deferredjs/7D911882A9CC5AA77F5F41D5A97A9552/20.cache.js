function Nrb(){}
function Rrb(){}
function csb(){}
function oZb(){}
function nZb(){}
function pZb(a,b,c){rZb(a,b,a.j.c);uZb(a,a.j.c-1,c)}
function rsb(){rsb=gAc;asb=new n7((k8(),new b8(CIc)),1,1)}
function jsb(){jsb=gAc;Urb=new n7((k8(),new b8(uIc)),15,16)}
function lsb(){lsb=gAc;Wrb=new n7((k8(),new b8(wIc)),16,16)}
function nsb(){nsb=gAc;Yrb=new n7((k8(),new b8(yIc)),16,16)}
function osb(){osb=gAc;Zrb=new n7((k8(),new b8(zIc)),16,16)}
function psb(){psb=gAc;$rb=new n7((k8(),new b8(AIc)),16,16)}
function qsb(){qsb=gAc;_rb=new n7((k8(),new b8(BIc)),16,16)}
function ssb(){ssb=gAc;bsb=new n7((k8(),new b8(DIc)),16,16)}
function hsb(){hsb=gAc;Srb=new n7((k8(),new b8(sIc)),32,32)}
function isb(){isb=gAc;Trb=new n7((k8(),new b8(tIc)),32,32)}
function ksb(){ksb=gAc;Vrb=new n7((k8(),new b8(vIc)),32,32)}
function msb(){msb=gAc;Xrb=new n7((k8(),new b8(xIc)),32,32)}
function Arb(a,b,c){Jec(a,Xgc(new Ygc(b.d,b.b,b.c,b.e,b.a)).a+XAc+c)}
function Orb(a,b,c,d,e){this.b=a;this.d=b;this.a=c;this.c=d;this.e=e}
function GZb(a){var b,c,d;b=Dl(a);d=b.children[1];c=d.children[1];return Dl(c)}
function uZb(a,b,c){var d,e;if(b>=a.j.c){return}e=FTb(a.a,b*2).children[0];d=Dl(e);xl(GZb(d),c)}
function wZb(a,b){if(b>=a.j.c||b<0||b==a.b){return}a.b>=0&&vZb(a,a.b,false);a.b=b;vZb(a,a.b,true)}
function EZb(){EZb=gAc;DZb=jP(d5,{124:1,135:1,138:1},1,['stackItemTop','stackItemMiddle'])}
function Sab(a){var b,c;b=tP(a.a.Vd(KIc),138);if(b==null){c=jP(d5,{124:1,135:1,138:1},1,[eIc,fIc,gIc,hIc,iIc]);a.a.Xd(KIc,c);return c}else{return b}}
function Rab(a){var b,c;b=tP(a.a.Vd(JIc),138);if(b==null){c=jP(d5,{124:1,135:1,138:1},1,[ZHc,$Hc,_Hc,aIc,bIc,cIc]);a.a.Xd(JIc,c);return c}else{return b}}
function Pab(a){var b,c;b=tP(a.a.Vd(HIc),138);if(b==null){c=jP(d5,{124:1,135:1,138:1},1,[HHc,IHc,JHc,KHc,LHc,MHc,NHc,OHc]);a.a.Xd(HIc,c);return c}else{return b}}
function Qab(a){var b,c;b=tP(a.a.Vd(IIc),138);if(b==null){c=jP(d5,{124:1,135:1,138:1},1,[QHc,RHc,SHc,THc,UHc,VHc,WHc,XHc]);a.a.Xd(IIc,c);return c}else{return b}}
function Crb(a){var b,c,d,e,f;f=new fgc;f.e[mEc]=4;for(c=Rab(a.a),d=0,e=c.length;d<e;++d){b=c[d];cgc(f,new _Vb(b))}return f}
function xZb(a,b){var c,d,e,f;for(f=b,c=a.j.c;f<c;++f){e=FTb(a.a,f*2);d=Dl(e);d[LIc]=f;b==0?Be(d,QIc,true):Be(d,QIc,false)}}
function qZb(a,b){var c,d;while(!!b&&b!=a.Q){c=tl(b,LIc);if(c!=null){d=sl(b,MIc);return d==Zj(a)?roc(c):-1}b=Fl(b)}return -1}
function tZb(a,b,c){var d,e,f;d=zUb(a,b);if(d){e=2*c;f=FTb(a.a,e);nl(a.a,f);f=FTb(a.a,e);nl(a.a,f);a.b==c?(a.b=-1):a.b>c&&--a.b;xZb(a,c)}return d}
function Drb(a,b){var c,d;c=new F3b;c.e[mEc]=0;E3b(c,(W2b(),U2b));C3b(c,new KQb(b));d=new I$b(a);d.Q[CAc]=jIc;C3b(c,d);return c.Q.outerHTML}
function FZb(){var a,b,c;b=Kl($doc,YDc);c=Kl($doc,ZDc);jl(b,l8b(c));b.style[DAc]=nCc;b[mEc]=0;b[nEc]=0;for(a=0;a<DZb.length;++a){jl(c,l8b(RZb(DZb[a])))}return b}
function sZb(a,b){var c,d,e,f,g;Dfc(a.Q,AAc,b);f=a.a.children.length>>1;for(e=0;e<f;++e){g=Dl(FTb(a.a,2*e));d=Dl(g);c=Dl(FTb(a.a,2*e+1));Dfc(g,b,'text-wrapper'+e);Dfc(c,b,OIc+e);Dfc(a.Ag(d),b,PIc+e)}}
function HZb(){var a;EZb();AUb.call(this);a=Kl($doc,YDc);this.Q=a;this.a=Kl($doc,ZDc);jl(a,l8b(this.a));a[mEc]=0;a[nEc]=0;yTb();KTb(a,1);this.Q[CAc]='gwt-StackPanel';Ce(this.Q,'gwt-DecoratedStackPanel')}
function vZb(a,b,c){var d,e,f,g,i;f=FTb(a.a,b*2);if(!f){return}d=Dl(f);Be(d,'gwt-StackPanelItem-selected',c);i=FTb(a.a,b*2+1);De(i,c);lgc(a.j,b).xb(c);g=FTb(a.a,(b+1)*2);if(g){e=Dl(g);Be(e,'gwt-StackPanelItem-below-selected',c)}}
function Brb(a){var b,c,d,e,f,g,i,j,k,n;j=new F3b;j.e[mEc]=5;C3b(j,new KQb((isb(),Trb)));d=new G$b;C3b(j,d);i=new Of(true,false);i.Pb(j);k=new fgc;k.e[mEc]=4;g=Pab(a.a);c=Qab(a.a);for(n=0;n<g.length;++n){f=g[n];b=c[n];e=new pVb(f);cgc(k,e);Ie(e,new Orb(d,f,b,e,i),(oq(),oq(),nq))}return k}
function rZb(a,b,c){var d,e,f,g,i;i=Kl($doc,iEc);f=Kl($doc,jEc);jl(i,l8b(f));jl(f,l8b(FZb()));g=Kl($doc,iEc);e=Kl($doc,jEc);jl(g,l8b(e));c=sUb(a,b,c);d=c*2;iSb(a.a,g,d);iSb(a.a,i,d);Be(f,'gwt-StackPanelItem',true);f[MIc]=Zj(a);f[BAc]=OFc;Be(e,'gwt-StackPanelContent',true);e[BAc]=nCc;e[NIc]=TBc;xUb(a,b,e,c,false);xZb(a,c);if(a.b==-1){wZb(a,0)}else{vZb(a,c,false);a.b>=c&&++a.b;vZb(a,a.b,true)}}
function Erb(a){var b,c,d,e,f,g,i,j;d=new csb;f=new HZb;f.Q.style[DAc]=kIc;e=Drb(mIc,(msb(),Xrb));pZb(f,(i=new mec(d),j=Jec(i.i,nIc),g=Sab(a.a),Arb(j,(lsb(),Wrb),g[0]),Arb(j,(jsb(),Urb),g[1]),Arb(j,(osb(),Zrb),g[2]),Arb(j,(nsb(),Yrb),g[3]),Arb(j,(psb(),$rb),g[4]),Vec(j,true,true),i),e);c=Drb(oIc,(ksb(),Vrb));pZb(f,Crb(a),c);b=Drb(pIc,(hsb(),Srb));pZb(f,Brb(a),b);sZb(f,'cwStackPanel');return f}
var LIc='__index',MIc='__owner',HIc='cwStackPanelContacts',IIc='cwStackPanelContactsEmails',JIc='cwStackPanelFilters',KIc='cwStackPanelMailFolders',QIc='gwt-StackPanelItem-first';_=Irb.prototype;_.ac=function Mrb(){d9(this.b,Erb(this.a))};_=Orb.prototype=Nrb.prototype=new Y;_.gC=function Prb(){return TW};_.oc=function Qrb(a){var b,c;E$b(this.b,this.d+qIc+this.a+rIc);b=$l(this.c.Q)+14;c=_l(this.c.Q)+14;Ff(this.e,b,c);this.e.Sb()};_.cM={22:1,44:1};_.a=null;_.b=null;_.c=null;_.d=null;_.e=null;_=csb.prototype=Rrb.prototype=new Y;_.gC=function dsb(){return UW};_.Ge=function esb(){return qsb(),_rb};_.Fe=function fsb(){return rsb(),asb};_.He=function gsb(){return ssb(),bsb};var Srb=null,Trb=null,Urb=null,Vrb=null,Wrb=null,Xrb=null,Yrb=null,Zrb=null,$rb=null,_rb=null,asb=null,bsb=null;_=oZb.prototype=new qUb;_.gC=function yZb(){return q1};_.Ag=function zZb(a){return a};_.Fb=function AZb(a){var b,c;if(xTb(a.type)==1){c=a.srcElement;b=qZb(this,c);b!=-1&&wZb(this,b)}Ne(this,a)};_.tb=function BZb(a){sZb(this,a)};_.Lb=function CZb(a){return tZb(this,a,mgc(this.j,a))};_.cM={40:1,46:1,84:1,91:1,92:1,95:1,110:1,112:1};_.a=null;_.b=-1;_=HZb.prototype=nZb.prototype=new oZb;_.gC=function IZb(){return j_};_.Ag=function JZb(a){return GZb(a)};_.cM={40:1,46:1,84:1,91:1,92:1,95:1,110:1,112:1};var DZb;var TW=foc(sFc,'CwStackPanel$2'),UW=foc(sFc,'CwStackPanel_Images_fr_InlineClientBundleGenerator'),q1=foc(WEc,'StackPanel'),j_=foc(WEc,'DecoratedStackPanel');yAc(sj)(20);
Ext.define('ASTD.Utils', {
    singleton: true,

    isSelected: function(dataview) {
        if (!dataview) {
            Ext.Msg.show({
                title: 'Предупреждение',
                msg: 'Не выбрана строка с данными',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING
            });
            return false;
        }
        else {
            return true;
        }
    },

    getParentCatalog: function(catalog, index) {
        while(catalog.parentNode.get('level') > index){
            catalog = catalog.parentNode;
        }
        return catalog;
    },

    getRootCatalog: function(catalog) {
        while(catalog.parentNode.get('type') !== 'ROOT'){
            catalog = catalog.parentNode;
        }
        return catalog.parentNode;
    },

    getVirtualCatalogs: function(records, index) {
        var virtualCatalogs = [];
        for(var i = index; i < records.length; i++){
            virtualCatalogs[i-1] = records[i].data ? records[i] : Ext.create('ASTD.model.CatalogModel', records[i]);
        }
        return virtualCatalogs;
    }
});
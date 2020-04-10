Ext.define('ASTD.AdvancedVTypes', {
    override: 'Ext.form.field.VTypes',

    listNum: function (value, field) {
        if (!value) {
            return true;
        }
        var viewModel = field.up('window').getViewModel();
        var fileList = viewModel.get('fileList');
        if (fileList) {  // can be called from history file form
            var store = fileList.getStore();
            var selectedFileId = viewModel.get('currentFile').get('id');
            if (store.getCount() > 0) {
                var index = store.findBy(function (record, id) {
                    return record.get('id') !== selectedFileId && record.get('listNum').trim() === value.trim();
                });
                if (index > -1) {  // found
                    return false;  // duplicate
                }
            }
            return true;
        }
        return true;
    },

    listNumText: 'Лист с таким номером уже существует.'
});
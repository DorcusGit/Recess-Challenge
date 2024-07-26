<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Administrator extends Model
{
    use HasFactory;

    protected $table = 'administrators'; // specify the table name

    protected $primaryKey = 'adminID'; // specify the primary key

    public $incrementing = true; // Indicates if the IDs are auto-incrementing

    protected $fillable = [
        'adminName',
        'password',
    ];

    protected $hidden = [
        'password',
    ];

    public function getPasswordAttribute($value)
    {
        return decrypt($value); // example of how you might handle encryption
    }

    public function setPasswordAttribute($value)
    {
        $this->attributes['password'] = encrypt($value); // example of how you might handle encryption
    }
}

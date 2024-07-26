<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class School extends Model
{
    use HasFactory;

    protected $table = 'schools'; // specify the table name

    protected $primaryKey = 'schoolRegNo'; // specify the primary key

    public $incrementing = false; // IDs are not auto-incrementing

    protected $keyType = 'int'; // specify the key type

    protected $fillable = [
        'schoolName',
        'district',
        'schoolRepID',
        'emailAddress',
        'password',
    ];

    protected $hidden = [
        'password',
    ];

    // Define any relationships or additional methods if needed
}
